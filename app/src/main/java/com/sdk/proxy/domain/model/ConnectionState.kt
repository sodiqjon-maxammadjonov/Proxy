package com.sdk.proxy.domain.model

data class ConnectionState(
    val isConnected: Boolean = false,
    val currentServer: VpnServer? = null,
    val connectionTime: Long = 0,
    val uploadSpeed: String = "0 KB/s",
    val downloadSpeed: String = "0 KB/s"
)