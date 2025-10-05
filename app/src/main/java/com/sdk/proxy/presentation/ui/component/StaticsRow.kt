package com.sdk.proxy.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsRow(
    uploadSpeed: String,
    downloadSpeed: String,
    isConnected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Upload,
            label = "Yuklash",
            value = if (isConnected) "2.5 MB/s" else "0 KB/s",
            color = Color(0xFFF59E0B)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Download,
            label = "Yuklab olish",
            value = if (isConnected) "15.8 MB/s" else "0 KB/s",
            color = Color(0xFF3B82F6)
        )
    }
}
