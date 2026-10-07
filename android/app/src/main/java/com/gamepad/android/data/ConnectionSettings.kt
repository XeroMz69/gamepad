package com.gamepad.android.data

data class ConnectionSettings(
    val pcIpAddress: String = "192.168.1.100",
    val port: Int = 26760,
    val autoReconnect: Boolean = true
)

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ERROR
}
