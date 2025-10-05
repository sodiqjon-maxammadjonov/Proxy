package com.sdk.proxy.domain.model

data class VpnServer(
    val id: String,
    val name: String,
    val countryCode: String,
    val flag: String,
    val ping: Int,
    val isPremium: Boolean = false,
    val ipAddress: String? = null,
    val port: Int = 443,
    val load: Int = 0
)