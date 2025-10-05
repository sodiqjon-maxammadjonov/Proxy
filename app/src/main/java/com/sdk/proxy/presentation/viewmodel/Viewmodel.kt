package com.sdk.proxy.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.proxy.data.service.ProxyVpnService
import com.sdk.proxy.domain.model.ConnectionState
import com.sdk.proxy.domain.model.VpnServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VpnUiState(
    val connectionState: ConnectionState = ConnectionState(),
    val servers: List<VpnServer> = emptyList(),
    val selectedServer: VpnServer? = null,
    val isDarkTheme: Boolean = false,
    val isLoading: Boolean = false
)

class VpnViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VpnUiState())
    val uiState: StateFlow<VpnUiState> = _uiState.asStateFlow()

    private var speedUpdateJob: kotlinx.coroutines.Job? = null

    init {
        loadServers()
    }

    private fun loadServers() {
        viewModelScope.launch {
            val servers = listOf(
                VpnServer("1", "United States", "US", "🇺🇸", 12),
                VpnServer("2", "United Kingdom", "GB", "🇬🇧", 45),
                VpnServer("3", "Germany", "DE", "🇩🇪", 38),
                VpnServer("4", "Japan", "JP", "🇯🇵", 89),
                VpnServer("5", "Singapore", "SG", "🇸🇬", 67),
                VpnServer("6", "Canada", "CA", "🇨🇦", 23),
                VpnServer("7", "France", "FR", "🇫🇷", 42),
                VpnServer("8", "Australia", "AU", "🇦🇺", 95, true)
            )
            _uiState.value = _uiState.value.copy(
                servers = servers,
                selectedServer = servers.first()
            )
        }
    }

    /**
     * VPN ga ulanish yoki uzish
     * Context va Activity kerak - VPN permission uchun
     */
    fun toggleConnection(context: Context, activity: Activity) {
        val currentState = _uiState.value.connectionState

        if (currentState.isConnected) {
            // VPN ni o'chirish
            disconnectVpn(context)
        } else {
            // VPN ga ulanish - avval permission so'rash
            requestVpnPermission(context, activity)
        }
    }

    /**
     * VPN permission so'rash
     */
    private fun requestVpnPermission(context: Context, activity: Activity) {
        val intent = VpnService.prepare(context)

        if (intent != null) {
            // Permission yo'q - so'rash kerak
            activity.startActivityForResult(intent, VPN_PERMISSION_REQUEST_CODE)
        } else {
            // Permission bor - to'g'ridan-to'g'ri ulanish
            connectVpn(context)
        }
    }

    /**
     * VPN xizmatini boshlash
     */
    fun connectVpn(context: Context) {
        val selectedServer = _uiState.value.selectedServer ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            // Kichik delay - yuklash animatsiyasi uchun
            delay(500)

            // VPN service ni boshlash
            val intent = Intent(context, ProxyVpnService::class.java).apply {
                action = ProxyVpnService.ACTION_CONNECT
                putExtra(ProxyVpnService.EXTRA_SERVER_ADDRESS, getServerIpAddress(selectedServer))
                putExtra(ProxyVpnService.EXTRA_SERVER_PORT, 443) // HTTPS port
            }

            context.startService(intent)

            // State ni yangilash
            _uiState.value = _uiState.value.copy(
                connectionState = _uiState.value.connectionState.copy(
                    isConnected = true,
                    currentServer = selectedServer
                ),
                isLoading = false
            )

            // Speed monitoring ni boshlash
            startSpeedMonitoring()
        }
    }

    /**
     * VPN ni uzish
     */
    private fun disconnectVpn(context: Context) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            // VPN service ni to'xtatish
            val intent = Intent(context, ProxyVpnService::class.java).apply {
                action = ProxyVpnService.ACTION_DISCONNECT
            }
            context.startService(intent)

            delay(300)

            // State ni yangilash
            _uiState.value = _uiState.value.copy(
                connectionState = _uiState.value.connectionState.copy(
                    isConnected = false,
                    currentServer = null,
                    uploadSpeed = "0 KB/s",
                    downloadSpeed = "0 KB/s"
                ),
                isLoading = false
            )

            // Speed monitoring ni to'xtatish
            stopSpeedMonitoring()
        }
    }

    /**
     * Speed ni real-time monitoring qilish
     */
    private fun startSpeedMonitoring() {
        speedUpdateJob?.cancel()
        speedUpdateJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Har soniyada yangilash

                val uploadSpeed = formatSpeed(ProxyVpnService.uploadSpeed)
                val downloadSpeed = formatSpeed(ProxyVpnService.downloadSpeed)

                _uiState.value = _uiState.value.copy(
                    connectionState = _uiState.value.connectionState.copy(
                        uploadSpeed = uploadSpeed,
                        downloadSpeed = downloadSpeed
                    )
                )
            }
        }
    }

    /**
     * Speed monitoring ni to'xtatish
     */
    private fun stopSpeedMonitoring() {
        speedUpdateJob?.cancel()
        speedUpdateJob = null
    }

    /**
     * Server IP manzilini olish (demo uchun)
     * Haqiqiy loyihada bu API dan keladi
     */
    private fun getServerIpAddress(server: VpnServer): String {
        return when (server.countryCode) {
            "US" -> "104.16.123.96"  // Cloudflare US
            "GB" -> "104.16.132.229" // Cloudflare UK
            "DE" -> "104.18.32.167"  // Cloudflare DE
            "JP" -> "104.18.40.119"  // Cloudflare JP
            "SG" -> "104.18.45.67"   // Cloudflare SG
            else -> "1.1.1.1"        // Cloudflare DNS
        }
    }

    /**
     * Speed ni formatlashtirish
     */
    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond < 1024 -> "$bytesPerSecond B/s"
            bytesPerSecond < 1024 * 1024 -> "${bytesPerSecond / 1024} KB/s"
            else -> "%.2f MB/s".format(bytesPerSecond.toDouble() / (1024 * 1024))
        }
    }

    fun selectServer(server: VpnServer) {
        _uiState.value = _uiState.value.copy(selectedServer = server)
    }

    fun toggleTheme() {
        _uiState.value = _uiState.value.copy(
            isDarkTheme = !_uiState.value.isDarkTheme
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopSpeedMonitoring()
    }

    companion object {
        const val VPN_PERMISSION_REQUEST_CODE = 100
    }
}