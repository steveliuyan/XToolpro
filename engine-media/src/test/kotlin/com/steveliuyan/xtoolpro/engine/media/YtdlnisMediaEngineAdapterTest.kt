package com.steveliuyan.xtoolpro.engine.media

import com.steveliuyan.xtoolpro.core.model.media.MediaCleanupStatus
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineCapability
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineFault
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineHealth
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineIdentity
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineOperation
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineRequest
import com.steveliuyan.xtoolpro.core.model.media.MediaEngineResult
import com.steveliuyan.xtoolpro.core.model.media.MediaRecoveryAction
import com.steveliuyan.xtoolpro.core.model.media.MediaUnavailableCapability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class YtdlnisMediaEngineAdapterTest {
    private val pinnedIdentity =
        MediaEngineIdentity(
            contractVersion = 1,
            ytdlnisCommit = "13320bb64f35c8d04f01bebfa782d7947758fb66",
            runtimeBundleVersion = "youtubedl-android-0.18.1+ffmpeg-0.17.2+aria2c-0.18.1",
            abi = "arm64-v8a",
            artifactManifestSha256 = "a".repeat(64),
        )

    @Test
    fun readyHandshakeReportsOnlyAdvertisedCapabilities() {
        val runtime = runtimeFor(
            MediaEngineCapability.PUBLIC_PARSE,
            MediaEngineCapability.PUBLIC_DOWNLOAD,
        )

        val handshake = runtime.handshake()

        assertEquals(MediaEngineHealth.READY, handshake.health)
        assertTrue(handshake.capabilities.contains(MediaEngineCapability.PUBLIC_PARSE))
        assertFalse(handshake.capabilities.contains(MediaEngineCapability.AUTHORIZED_SESSION_DOWNLOAD))
    }

    @Test
    fun completedOperationMapsToSuccessWithoutMediaContent() {
        val adapter = adapterFor(
            runtimeResult = YtdlnisRuntimeResult.Completed(producedItemCount = 1),
            capabilities = setOf(MediaEngineCapability.PUBLIC_PARSE),
        )

        val result = adapter.execute(request(MediaEngineOperation.PARSE_PUBLIC, "task-success"))

        assertEquals(
            MediaEngineResult.Success(
                taskId = "task-success",
                identity = pinnedIdentity,
                operation = MediaEngineOperation.PARSE_PUBLIC,
                producedItemCount = 1,
            ),
            result,
        )
    }

    @Test
    fun unavailableCapabilityPreventsRuntimeExecution() {
        var executed = false
        val runtime = object : YtdlnisRuntime {
            override fun handshake() =
                YtdlnisRuntimeHandshake(
                    identity = pinnedIdentity,
                    health = MediaEngineHealth.READY,
                    capabilities = emptySet(),
                )

            override fun execute(request: MediaEngineRequest): YtdlnisRuntimeResult {
                executed = true
                return YtdlnisRuntimeResult.Completed(producedItemCount = 1)
            }
        }
        val adapter = YtdlnisMediaEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request(MediaEngineOperation.DOWNLOAD_PUBLIC, "task-unavailable"))

        assertEquals(
            MediaEngineResult.Unavailable(
                taskId = "task-unavailable",
                capability = MediaUnavailableCapability.PUBLIC_DOWNLOAD,
                recoveryAction = MediaRecoveryAction.INSTALL_PINNED_RUNTIME,
            ),
            result,
        )
        assertFalse(executed)
    }

    @Test
    fun cancelledOperationKeepsCleanupReceipt() {
        val adapter = adapterFor(
            runtimeResult = YtdlnisRuntimeResult.Cancelled(
                confirmedAtEpochMillis = 1_725_000_000_000,
                cleanupStatus = MediaCleanupStatus.COMPLETED,
            ),
            capabilities = setOf(MediaEngineCapability.PUBLIC_PARSE),
        )

        val result = adapter.execute(request(MediaEngineOperation.PARSE_PUBLIC, "task-cancelled"))

        assertEquals(
            MediaEngineResult.Cancelled(
                taskId = "task-cancelled",
                confirmedAtEpochMillis = 1_725_000_000_000,
                cleanupStatus = MediaCleanupStatus.COMPLETED,
            ),
            result,
        )
    }

    @Test
    fun runtimeCrashMapsToSanitizedFault() {
        val adapter = adapterFor(
            runtimeResult = YtdlnisRuntimeResult.Crashed(MediaEngineFault.NATIVE_PROCESS_TERMINATED),
            capabilities = setOf(MediaEngineCapability.PUBLIC_PARSE),
        )

        val result = adapter.execute(request(MediaEngineOperation.PARSE_PUBLIC, "task-crashed"))

        assertEquals(
            MediaEngineResult.EngineCrashed(
                taskId = "task-crashed",
                identity = pinnedIdentity,
                fault = MediaEngineFault.NATIVE_PROCESS_TERMINATED,
            ),
            result,
        )
    }

    @Test
    fun identityMismatchStopsBeforeRuntimeExecution() {
        var executed = false
        val actualIdentity = pinnedIdentity.copy(artifactManifestSha256 = "b".repeat(64))
        val runtime = object : YtdlnisRuntime {
            override fun handshake() =
                YtdlnisRuntimeHandshake(
                    identity = actualIdentity,
                    health = MediaEngineHealth.READY,
                    capabilities = setOf(MediaEngineCapability.PUBLIC_PARSE),
                )

            override fun execute(request: MediaEngineRequest): YtdlnisRuntimeResult {
                executed = true
                return YtdlnisRuntimeResult.Completed(producedItemCount = 1)
            }
        }
        val adapter = YtdlnisMediaEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request(MediaEngineOperation.PARSE_PUBLIC, "task-version-mismatch"))

        assertEquals(
            MediaEngineResult.VersionMismatch(
                taskId = "task-version-mismatch",
                expected = pinnedIdentity,
                actual = actualIdentity,
            ),
            result,
        )
        assertFalse(executed)
    }

    private fun adapterFor(
        runtimeResult: YtdlnisRuntimeResult,
        capabilities: Set<MediaEngineCapability>,
    ): YtdlnisMediaEngineAdapter =
        YtdlnisMediaEngineAdapter(
            pinnedIdentity = pinnedIdentity,
            runtime = runtimeFor(capabilities, runtimeResult),
        )

    private fun runtimeFor(
        vararg capabilities: MediaEngineCapability,
    ): YtdlnisRuntime = runtimeFor(capabilities.toSet(), YtdlnisRuntimeResult.Completed(producedItemCount = 0))

    private fun runtimeFor(
        capabilities: Set<MediaEngineCapability>,
        result: YtdlnisRuntimeResult,
    ): YtdlnisRuntime =
        object : YtdlnisRuntime {
            override fun handshake() =
                YtdlnisRuntimeHandshake(
                    identity = pinnedIdentity,
                    health = MediaEngineHealth.READY,
                    capabilities = capabilities,
                )

            override fun execute(request: MediaEngineRequest) = result
        }

    private fun request(operation: MediaEngineOperation, taskId: String) =
        MediaEngineRequest(taskId = taskId, operation = operation)
}
