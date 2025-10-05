package com.sdk.proxy.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sdk.proxy.presentation.ui.component.ConnectionInfoCard
import com.sdk.proxy.presentation.ui.component.ConnectionStatusCard
import com.sdk.proxy.presentation.ui.component.ServerBottomSheet
import com.sdk.proxy.presentation.ui.component.ServerSelectionCard
import com.sdk.proxy.presentation.ui.component.SettingsBottomSheet
import com.sdk.proxy.presentation.ui.component.StatisticsRow
import com.sdk.proxy.presentation.viewmodel.VpnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnHomeScreen(
    viewModel: VpnViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showServerSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "SecureVPN",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Sozlamalar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Connection Status Card
            ConnectionStatusCard(
                isConnected = uiState.connectionState.isConnected,
                onToggleConnection = { viewModel.toggleConnection() }
            )

            // Server Selection Card
            ServerSelectionCard(
                selectedServer = uiState.selectedServer,
                onClick = { showServerSheet = true }
            )

            // Statistics Cards
            StatisticsRow(
                uploadSpeed = uiState.connectionState.uploadSpeed,
                downloadSpeed = uiState.connectionState.downloadSpeed,
                isConnected = uiState.connectionState.isConnected
            )

            if (uiState.connectionState.isConnected) {
                ConnectionInfoCard()
            }
        }

        // Server Selection Sheet
        if (showServerSheet) {
            ServerBottomSheet(
                servers = uiState.servers,
                selectedServer = uiState.selectedServer,
                onSelectServer = { server ->
                    viewModel.selectServer(server)
                    showServerSheet = false
                },
                onDismiss = { showServerSheet = false }
            )
        }

        // Settings Sheet
        if (showSettingsSheet) {
            SettingsBottomSheet(
                isDarkTheme = uiState.isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme() },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
