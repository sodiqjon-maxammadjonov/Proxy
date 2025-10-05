package com.sdk.proxy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var viewModel: VpnViewModel

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.connectVpn(this)
        } else {
            // Permission rad etildi
            // Bu yerda xabar ko'rsatish mumkin
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            viewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            SecureVPNTheme(darkTheme = uiState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VpnHomeScreen(
                        viewModel = viewModel,
                        activity = this
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VpnViewModel.VPN_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.connectVpn(this)
            }
        }
    }
}