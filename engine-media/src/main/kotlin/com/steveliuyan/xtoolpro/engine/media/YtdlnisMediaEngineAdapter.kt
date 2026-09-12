package com.steveliuyan.xtoolpro.engine.media

import com.steveliuyan.xtoolpro.core.model.media.MediaCleanupStatus
import com.steveliuyan.xtoolpro.core.model.media.MediaEngine
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineCapability
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineFault
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineHandshake
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineHealth
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineIdentity
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineRequest
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineResult
import com.steveliuyan.xtoolpro.core.model.media.MediaRecoveryAction
import com.steveliuyan.xtoolpro.core.model.media.MediaUnavailableCapability

interface YtdlnisRuntime {
    fun handshake(): YtdlnisRuntimeHandshake

    fun execute(request: MediaEngineRequest): YtdlnisRuntimeResult
}

typealias YtdlnisRuntimeHandshake = MediaEngineHandshake

sealed interface YtdlnisRuntimeResult {
    data class Completed(
        val producedItemCount: Int,
    ) : YtdlnisRuntimeResult {
        init {
            require(producedItemCount >= 0) { "producedItemCount must not be negative" }
        }
    }

    data class Unavailable(
        val capability: MediaUnavailableCapability,
        val recoveryAction: MediaRecoveryAction,
    ) : YtdlnisRuntimeResult

    data class Cancelled(
        val confirmedAtEpochMillis: Long,
        val cleanupStatus: MediaCleanupStatus,
    ) : YtdlnisRuntimeResult

    data class Crashed(
        val fault: MediaEngineFault,
    ) : YtdlnisRuntimeResult
}

class YtdlnisMediaEngineAdapter(
    private val pinnedIdentity: MediaEngineIdentity,
    private val runtime: YtdlnisRuntime,
) : MediaEngine {
    override fun execute(request: MediaEngineRequest): MediaEngineResult {
        val handshake =
            try {
                runtime.handshake()
            } catch (_: LinkageError) {
                return MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = pinnedIdentity,
                    fault = MediaEngineFault.NATIVE_PROCESS_TERMINATED,
                )
            } catch (_: RuntimeException) {
                return MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = pinnedIdentity,
                    fault = MediaEngineFault.RUNTIME_FAILURE,
                )
            }

        if (handshake.identity != pinnedIdentity) {
            return MediaEngineResult.VersionMismatch(
                taskId = request.taskId,
                expected = pinnedIdentity,
                actual = handshake.identity,
            )
        }

        when (handshake.health) {
            MediaEngineHealth.UNAVAILABLE ->
                return MediaEngineResult.Unavailable(
                    taskId = request.taskId,
                    capability = MediaUnavailableCapability.ENGINE_RUNTIME,
                    recoveryAction = MediaRecoveryAction.INSTALL_PINNED_RUNTIME,
                )

            MediaEngineHealth.CRASHED ->
                return MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = handshake.identity,
                    fault = MediaEngineFault.NATIVE_PROCESS_TERMINATED,
                )

            MediaEngineHealth.READY -> Unit
        }

        if (request.operation.requiredCapability !in handshake.capabilities) {
            return MediaEngineResult.Unavailable(
                taskId = request.taskId,
                capability = request.operation.requiredCapability.toUnavailableCapability(),
                recoveryAction =
                    if (request.operation.requiredCapability == MediaEngineCapability.AUTHORIZED_SESSION_DOWNLOAD) {
                        MediaRecoveryAction.REQUEST_AUTHORIZED_SESSION
                    } else {
                        MediaRecoveryAction.INSTALL_PINNED_RUNTIME
                    },
            )
        }

        val runtimeResult =
            try {
                runtime.execute(request)
            } catch (_: LinkageError) {
                return MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = handshake.identity,
                    fault = MediaEngineFault.NATIVE_PROCESS_TERMINATED,
                )
            } catch (_: RuntimeException) {
                return MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = handshake.identity,
                    fault = MediaEngineFault.RUNTIME_FAILURE,
                )
            }

        return when (runtimeResult) {
            is YtdlnisRuntimeResult.Completed ->
                MediaEngineResult.Success(
                    taskId = request.taskId,
                    identity = handshake.identity,
                    operation = request.operation,
                    producedItemCount = runtimeResult.producedItemCount,
                )

            is YtdlnisRuntimeResult.Unavailable ->
                MediaEngineResult.Unavailable(
                    taskId = request.taskId,
                    capability = runtimeResult.capability,
                    recoveryAction = runtimeResult.recoveryAction,
                )

            is YtdlnisRuntimeResult.Cancelled ->
                MediaEngineResult.Cancelled(
                    taskId = request.taskId,
                    confirmedAtEpochMillis = runtimeResult.confirmedAtEpochMillis,
                    cleanupStatus = runtimeResult.cleanupStatus,
                )

            is YtdlnisRuntimeResult.Crashed ->
                MediaEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = handshake.identity,
                    fault = runtimeResult.fault,
                )
        }
    }

    private fun MediaEngineCapability.toUnavailableCapability(): MediaUnavailableCapability =
        when (this) {
            MediaEngineCapability.PUBLIC_PARSE -> MediaUnavailableCapability.PUBLIC_PARSE
            MediaEngineCapability.PUBLIC_DOWNLOAD -> MediaUnavailableCapability.PUBLIC_DOWNLOAD
            MediaEngineCapability.AUTHORIZED_SESSION_DOWNLOAD -> MediaUnavailableCapability.AUTHORIZED_SESSION_DOWNLOAD
            MediaEngineCapability.PLAYLIST_SELECTION -> MediaUnavailableCapability.PUBLIC_DOWNLOAD
            MediaEngineCapability.FORMAT_SELECTION -> MediaUnavailableCapability.PUBLIC_DOWNLOAD
        }
}
