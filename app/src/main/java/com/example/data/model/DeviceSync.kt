package com.example.data.model

enum class DeviceType {
    PHONE,
    TABLET,
    WEB,
    SMART_SPEAKER,
    CAR
}

data class DeviceSync(
    val id: String,
    val name: String,
    val type: DeviceType,
    val isCurrent: Boolean,
    val isConnected: Boolean,
    val lastSyncTime: String,
    val activeTrackTitle: String? = null
)
