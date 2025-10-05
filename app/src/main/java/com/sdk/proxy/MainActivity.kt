package com.sdk.proxy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sdk.proxy.presentation.ui.screen.VpnHomeScreen
import com.sdk.proxy.presentation.viewmodel.VpnViewModel
import com.sdk.proxy.ui.theme.SecureVPNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: VpnViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            SecureVPNTheme(darkTheme = uiState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VpnHomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}