package com.sdk.proxy.domain.model

data class ConnectionState(
    val isConnected: Boolean = false,
    val currentServer: VpnServer? = null,
    val uploadSpeed: String = "0 B/s",
    val downloadSpeed: String = "0 B/s",
    val connectionTime: Long = 0L, // Milliseconds
    val bytesUploaded: Long = 0L,
    val bytesDownloaded: Long = 0L
)
