package com.steveliuyan.xtoolpro.core.model.media

data class MediaEngineIdentity(
    val contractVersion: Int,
    val ytdlnisCommit: String,
    val runtimeBundleVersion: String,
    val abi: String,
    val artifactManifestSha256: String,
) {
    init {
        require(contractVersion > 0) { "contractVersion must be positive" }
        require(ytdlnisCommit.isNotBlank()) { "ytdlnisCommit must not be blank" }
        require(runtimeBundleVersion.isNotBlank()) { "runtimeBundleVersion must not be blank" }
        require(abi.isNotBlank()) { "abi must not be blank" }
        require(artifactManifestSha256.matches(Regex("[0-9a-fA-F]{64}"))) {
            "artifactManifestSha256 must be a SHA-256 hex digest"
        }
    }
}

enum class MediaEngineCapability {
    PUBLIC_PARSE,
    PUBLIC_DOWNLOAD,
    AUTHORIZED_SESSION_DOWNLOAD,
    PLAYLIST_SELECTION,
    FORMAT_SELECTION,
}

enum class MediaEngineHealth {
    READY,
    UNAVAILABLE,
    CRASHED,
}

data class MediaEngineHandshake(
    val identity: MediaEngineIdentity,
    val health: MediaEngineHealth,
    val capabilities: Set<MediaEngineCapability>,
)

enum class MediaEngineOperation(
    val requiredCapability: MediaEngineCapability,
) {
    PARSE_PUBLIC(MediaEngineCapability.PUBLIC_PARSE),
    DOWNLOAD_PUBLIC(MediaEngineCapability.PUBLIC_DOWNLOAD),
    DOWNLOAD_AUTHORIZED_SESSION(MediaEngineCapability.AUTHORIZED_SESSION_DOWNLOAD),
}

data class MediaEngineRequest(
    val taskId: String,
    val operation: MediaEngineOperation,
) {
    init {
        require(taskId.isNotBlank()) { "taskId must not be blank" }
    }
}

enum class MediaUnavailableCapability {
    ENGINE_RUNTIME,
    PUBLIC_PARSE,
    PUBLIC_DOWNLOAD,
    AUTHORIZED_SESSION_DOWNLOAD,
}

enum class MediaRecoveryAction {
    INSTALL_PINNED_RUNTIME,
    REQUEST_AUTHORIZED_SESSION,
    RETRY_AFTER_ENGINE_READY,
}

enum class MediaCleanupStatus {
    NOT_REQUIRED,
    COMPLETED,
    FAILED,
}

enum class MediaEngineFault {
    NATIVE_PROCESS_TERMINATED,
    RUNTIME_FAILURE,
}

private fun requireTaskId(taskId: String) {
    require(taskId.isNotBlank()) { "taskId must not be blank" }
}

sealed interface MediaEngineResult {
    val taskId: String

    data class Success(
        override val taskId: String,
        val identity: MediaEngineIdentity,
        val operation: MediaEngineOperation,
        val producedItemCount: Int,
    ) : MediaEngineResult {
        init {
            requireTaskId(taskId)
            require(producedItemCount >= 0) { "producedItemCount must not be negative" }
        }
    }

    data class Unavailable(
        override val taskId: String,
        val capability: MediaUnavailableCapability,
        val recoveryAction: MediaRecoveryAction,
    ) : MediaEngineResult {
        init {
            requireTaskId(taskId)
        }
    }

    data class Cancelled(
        override val taskId: String,
        val confirmedAtEpochMillis: Long,
        val cleanupStatus: MediaCleanupStatus,
    ) : MediaEngineResult {
        init {
            requireTaskId(taskId)
            require(confirmedAtEpochMillis > 0) { "confirmedAtEpochMillis must be positive" }
        }
    }

    data class EngineCrashed(
        override val taskId: String,
        val identity: MediaEngineIdentity,
        val fault: MediaEngineFault,
    ) : MediaEngineResult {
        init {
            requireTaskId(taskId)
        }
    }

    data class VersionMismatch(
        override val taskId: String,
        val expected: MediaEngineIdentity,
        val actual: MediaEngineIdentity,
    ) : MediaEngineResult {
        init {
            requireTaskId(taskId)
            require(expected != actual) { "expected and actual identities must differ" }
        }
    }
}

interface MediaEngine {
    fun execute(request: MediaEngineRequest): MediaEngineResult
}
