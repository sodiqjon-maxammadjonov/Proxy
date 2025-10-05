package com.sdk.proxy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdk.proxy.domain.model.ConnectionState
import com.sdk.proxy.domain.model.VpnServer
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

    fun toggleConnection() {
        val currentState = _uiState.value.connectionState
        _uiState.value = _uiState.value.copy(
            connectionState = currentState.copy(
                isConnected = !currentState.isConnected,
                currentServer = if (!currentState.isConnected) _uiState.value.selectedServer else null
            )
        )
    }

    fun selectServer(server: VpnServer) {
        _uiState.value = _uiState.value.copy(selectedServer = server)
    }

    fun toggleTheme() {
        _uiState.value = _uiState.value.copy(
            isDarkTheme = !_uiState.value.isDarkTheme
        )
    }
}
