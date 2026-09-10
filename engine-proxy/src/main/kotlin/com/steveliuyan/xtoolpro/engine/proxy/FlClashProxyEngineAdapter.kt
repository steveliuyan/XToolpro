package com.steveliuyan.xtoolpro.engine.proxy

import com.steveliuyan.xtoolpro.core.model.proxy.ProxyCleanupStatus
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyConnectionState
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngine
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineFault
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineIdentity
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineOperation
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineRequest
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineResult
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyRecoveryAction
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyUnavailableCapability

interface FlClashRuntime {
    fun identity(): ProxyEngineIdentity

    fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult
}

sealed interface FlClashRuntimeResult {
    data class Completed(
        val connectionState: ProxyConnectionState,
    ) : FlClashRuntimeResult

    data class Unavailable(
        val capability: ProxyUnavailableCapability,
        val recoveryAction: ProxyRecoveryAction,
    ) : FlClashRuntimeResult

    data class Cancelled(
        val confirmedAtEpochMillis: Long,
        val cleanupStatus: ProxyCleanupStatus,
    ) : FlClashRuntimeResult

    data class Crashed(
        val fault: ProxyEngineFault,
    ) : FlClashRuntimeResult
}

class FlClashProxyEngineAdapter(
    private val pinnedIdentity: ProxyEngineIdentity,
    private val runtime: FlClashRuntime,
) : ProxyEngine {
    override fun execute(request: ProxyEngineRequest): ProxyEngineResult {
        val actualIdentity =
            try {
                runtime.identity()
            } catch (_: LinkageError) {
                return ProxyEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = pinnedIdentity,
                    fault = ProxyEngineFault.NATIVE_PROCESS_TERMINATED,
                )
            } catch (_: RuntimeException) {
                return ProxyEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = pinnedIdentity,
                    fault = ProxyEngineFault.RUNTIME_FAILURE,
                )
            }
        if (actualIdentity != pinnedIdentity) {
            return ProxyEngineResult.VersionMismatch(
                taskId = request.taskId,
                expected = pinnedIdentity,
                actual = actualIdentity,
            )
        }

        val runtimeResult =
            try {
                runtime.execute(request.operation)
            } catch (_: LinkageError) {
                return ProxyEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = actualIdentity,
                    fault = ProxyEngineFault.NATIVE_PROCESS_TERMINATED,
                )
            } catch (_: RuntimeException) {
                return ProxyEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = actualIdentity,
                    fault = ProxyEngineFault.RUNTIME_FAILURE,
                )
            }

        return when (runtimeResult) {
            is FlClashRuntimeResult.Completed ->
                ProxyEngineResult.Success(
                    taskId = request.taskId,
                    identity = actualIdentity,
                    connectionState = runtimeResult.connectionState,
                )

            is FlClashRuntimeResult.Unavailable ->
                ProxyEngineResult.Unavailable(
                    taskId = request.taskId,
                    capability = runtimeResult.capability,
                    recoveryAction = runtimeResult.recoveryAction,
                )

            is FlClashRuntimeResult.Cancelled ->
                ProxyEngineResult.Cancelled(
                    taskId = request.taskId,
                    confirmedAtEpochMillis = runtimeResult.confirmedAtEpochMillis,
                    cleanupStatus = runtimeResult.cleanupStatus,
                )

            is FlClashRuntimeResult.Crashed ->
                ProxyEngineResult.EngineCrashed(
                    taskId = request.taskId,
                    identity = actualIdentity,
                    fault = runtimeResult.fault,
                )
        }
    }
}
