package com.aeci.mmucompanion.domain.model

enum class SyncStatus {
    OFFLINE,
    SYNCING,
    SYNCED,
    FAILED
}

data class SyncableItem(
    val id: String,
    val type: SyncItemType,
    val data: String,
    val timestamp: Long,
    val isSynced: Boolean = false,
    val retryCount: Int = 0,
    val lastError: String? = null
)

enum class SyncItemType {
    FORM_SUBMISSION,
    EQUIPMENT_UPDATE,
    USER_UPDATE,
    TASK_UPDATE
}

data class SyncResult(
    val isSuccess: Boolean,
    val syncedItems: Int,
    val failedItems: Int,
    val errors: List<String> = emptyList()
) 