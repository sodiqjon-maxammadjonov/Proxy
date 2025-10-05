package com.sdk.proxy.domain.model

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val ping: Int,
    val isPremium: Boolean = false
)