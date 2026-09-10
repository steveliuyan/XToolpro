package com.steveliuyan.xtoolpro.core.model.proxy

data class ProxyEngineIdentity(
    val contractVersion: Int,
    val flClashCommit: String,
    val clashMetaCommit: String,
    val abi: String,
    val artifactManifestSha256: String,
) {
    init {
        require(contractVersion > 0) { "contractVersion must be positive" }
        require(flClashCommit.isNotBlank()) { "flClashCommit must not be blank" }
        require(clashMetaCommit.isNotBlank()) { "clashMetaCommit must not be blank" }
        require(abi.isNotBlank()) { "abi must not be blank" }
        require(artifactManifestSha256.matches(Regex("[0-9a-fA-F]{64}"))) {
            "artifactManifestSha256 must be a SHA-256 hex digest"
        }
    }
}

enum class ProxyEngineOperation {
    START,
    STOP,
}

data class ProxyEngineRequest(
    val taskId: String,
    val operation: ProxyEngineOperation,
) {
    init {
        require(taskId.isNotBlank()) { "taskId must not be blank" }
    }
}

enum class ProxyConnectionState {
    CONNECTED,
    DISCONNECTED,
}

enum class ProxyUnavailableCapability {
    PINNED_CORE,
    VPN_PERMISSION,
    VPN_SLOT,
}

enum class ProxyRecoveryAction {
    INSTALL_PINNED_ENGINE,
    REQUEST_VPN_PERMISSION,
    STOP_COMPETING_VPN,
}

enum class ProxyCleanupStatus {
    NOT_REQUIRED,
    COMPLETED,
    FAILED,
}

enum class ProxyEngineFault {
    NATIVE_PROCESS_TERMINATED,
    RUNTIME_FAILURE,
}

sealed interface ProxyEngineResult {
    val taskId: String

    data class Success(
        override val taskId: String,
        val identity: ProxyEngineIdentity,
        val connectionState: ProxyConnectionState,
    ) : ProxyEngineResult

    data class Unavailable(
        override val taskId: String,
        val capability: ProxyUnavailableCapability,
        val recoveryAction: ProxyRecoveryAction,
    ) : ProxyEngineResult

    data class Cancelled(
        override val taskId: String,
        val confirmedAtEpochMillis: Long,
        val cleanupStatus: ProxyCleanupStatus,
    ) : ProxyEngineResult

    data class EngineCrashed(
        override val taskId: String,
        val identity: ProxyEngineIdentity,
        val fault: ProxyEngineFault,
    ) : ProxyEngineResult

    data class VersionMismatch(
        override val taskId: String,
        val expected: ProxyEngineIdentity,
        val actual: ProxyEngineIdentity,
    ) : ProxyEngineResult
}

interface ProxyEngine {
    fun execute(request: ProxyEngineRequest): ProxyEngineResult
}
