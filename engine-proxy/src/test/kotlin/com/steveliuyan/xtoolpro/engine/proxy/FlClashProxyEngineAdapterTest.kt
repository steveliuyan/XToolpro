package com.steveliuyan.xtoolpro.engine.proxy

import com.steveliuyan.xtoolpro.core.model.proxy.ProxyCleanupStatus
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyConnectionState
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineFault
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineIdentity
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineOperation
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineRequest
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyEngineResult
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyRecoveryAction
import com.steveliuyan.xtoolpro.core.model.proxy.ProxyUnavailableCapability
import org.junit.Assert.assertEquals
import org.junit.Test

class FlClashProxyEngineAdapterTest {
    private val pinnedIdentity =
        ProxyEngineIdentity(
            contractVersion = 1,
            flClashCommit = "62addf738a76b1a492e19af2dbabdb6d572b9e72",
            clashMetaCommit = "0f7f05adff5e2c49775a112dcfe05a6aa36fda0c",
            abi = "arm64-v8a",
            artifactManifestSha256 =
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        )

    @Test
    fun completedRuntimeOperationMapsToSuccess() {
        val adapter =
            adapterFor(
                FlClashRuntimeResult.Completed(ProxyConnectionState.CONNECTED),
            )

        val result = adapter.execute(request("task-success"))

        assertEquals(
            ProxyEngineResult.Success(
                taskId = "task-success",
                identity = pinnedIdentity,
                connectionState = ProxyConnectionState.CONNECTED,
            ),
            result,
        )
    }

    @Test
    fun missingVpnPermissionMapsToUnavailableWithRecovery() {
        val adapter =
            adapterFor(
                FlClashRuntimeResult.Unavailable(
                    capability = ProxyUnavailableCapability.VPN_PERMISSION,
                    recoveryAction = ProxyRecoveryAction.REQUEST_VPN_PERMISSION,
                ),
            )

        val result = adapter.execute(request("task-unavailable"))

        assertEquals(
            ProxyEngineResult.Unavailable(
                taskId = "task-unavailable",
                capability = ProxyUnavailableCapability.VPN_PERMISSION,
                recoveryAction = ProxyRecoveryAction.REQUEST_VPN_PERMISSION,
            ),
            result,
        )
    }

    @Test
    fun cancelledRuntimeOperationKeepsCleanupReceipt() {
        val adapter =
            adapterFor(
                FlClashRuntimeResult.Cancelled(
                    confirmedAtEpochMillis = 1_725_000_000_000,
                    cleanupStatus = ProxyCleanupStatus.COMPLETED,
                ),
            )

        val result = adapter.execute(request("task-cancelled"))

        assertEquals(
            ProxyEngineResult.Cancelled(
                taskId = "task-cancelled",
                confirmedAtEpochMillis = 1_725_000_000_000,
                cleanupStatus = ProxyCleanupStatus.COMPLETED,
            ),
            result,
        )
    }

    @Test
    fun runtimeCrashMapsToSanitizedEngineFault() {
        val adapter =
            adapterFor(
                FlClashRuntimeResult.Crashed(ProxyEngineFault.NATIVE_PROCESS_TERMINATED),
            )

        val result = adapter.execute(request("task-crashed"))

        assertEquals(
            ProxyEngineResult.EngineCrashed(
                taskId = "task-crashed",
                identity = pinnedIdentity,
                fault = ProxyEngineFault.NATIVE_PROCESS_TERMINATED,
            ),
            result,
        )
    }

    @Test
    fun unexpectedRuntimeFailureDoesNotEscapeTheEngineBoundary() {
        val runtime =
            object : FlClashRuntime {
                override fun identity() = pinnedIdentity

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    throw IllegalStateException("sensitive runtime detail")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-runtime-failure"))

        assertEquals(
            ProxyEngineResult.EngineCrashed(
                taskId = "task-runtime-failure",
                identity = pinnedIdentity,
                fault = ProxyEngineFault.RUNTIME_FAILURE,
            ),
            result,
        )
    }

    @Test
    fun nativeLinkageFailureMapsToSanitizedEngineFault() {
        val runtime =
            object : FlClashRuntime {
                override fun identity() = pinnedIdentity

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    throw UnsatisfiedLinkError("sensitive native linkage detail")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-native-linkage"))

        assertEquals(
            ProxyEngineResult.EngineCrashed(
                taskId = "task-native-linkage",
                identity = pinnedIdentity,
                fault = ProxyEngineFault.NATIVE_PROCESS_TERMINATED,
            ),
            result,
        )
    }

    @Test
    fun identityLinkageFailureMapsToSanitizedEngineFault() {
        val runtime =
            object : FlClashRuntime {
                override fun identity(): ProxyEngineIdentity = throw ExceptionInInitializerError("sensitive identity linkage detail")

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    error("runtime must not execute when identity negotiation fails")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-identity-linkage"))

        assertEquals(
            ProxyEngineResult.EngineCrashed(
                taskId = "task-identity-linkage",
                identity = pinnedIdentity,
                fault = ProxyEngineFault.NATIVE_PROCESS_TERMINATED,
            ),
            result,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankTaskIdIsRejectedAtRequestBoundary() {
        ProxyEngineRequest(
            taskId = "   ",
            operation = ProxyEngineOperation.START,
        )
    }

    @Test
    fun versionMismatchBlocksRuntimeExecution() {
        val runtime =
            object : FlClashRuntime {
                override fun identity() = pinnedIdentity.copy(contractVersion = 2)

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    error("runtime must not execute after a version mismatch")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-version"))

        assertEquals(
            ProxyEngineResult.VersionMismatch(
                taskId = "task-version",
                expected = pinnedIdentity,
                actual = pinnedIdentity.copy(contractVersion = 2),
            ),
            result,
        )
    }

    @Test
    fun abiMismatchBlocksRuntimeExecution() {
        val actualIdentity = pinnedIdentity.copy(abi = "armeabi-v7a")
        val runtime =
            object : FlClashRuntime {
                override fun identity() = actualIdentity

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    error("runtime must not execute with an unpinned ABI")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-abi-version"))

        assertEquals(
            ProxyEngineResult.VersionMismatch(
                taskId = "task-abi-version",
                expected = pinnedIdentity,
                actual = actualIdentity,
            ),
            result,
        )
    }

    @Test
    fun artifactManifestMismatchBlocksRuntimeExecution() {
        val actualIdentity =
            pinnedIdentity.copy(
                artifactManifestSha256 =
                    "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
            )
        val runtime =
            object : FlClashRuntime {
                override fun identity() = actualIdentity

                override fun execute(operation: ProxyEngineOperation): FlClashRuntimeResult =
                    error("runtime must not execute with an unpinned artifact manifest")
            }
        val adapter = FlClashProxyEngineAdapter(pinnedIdentity, runtime)

        val result = adapter.execute(request("task-artifact-version"))

        assertEquals(
            ProxyEngineResult.VersionMismatch(
                taskId = "task-artifact-version",
                expected = pinnedIdentity,
                actual = actualIdentity,
            ),
            result,
        )
    }

    private fun request(taskId: String) =
        ProxyEngineRequest(
            taskId = taskId,
            operation = ProxyEngineOperation.START,
        )

    private fun adapterFor(result: FlClashRuntimeResult): FlClashProxyEngineAdapter =
        FlClashProxyEngineAdapter(
            pinnedIdentity = pinnedIdentity,
            runtime =
                object : FlClashRuntime {
                    override fun identity() = pinnedIdentity

                    override fun execute(operation: ProxyEngineOperation) = result
                },
        )
}
