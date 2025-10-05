package com.sdk.proxy.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsRow(
    uploadSpeed: String,
    downloadSpeed: String,
    isConnected: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Upload Card
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Upload,
            label = "Yuklash",
            value = uploadSpeed,
            color = Color(0xFF3B82F6),
            isActive = isConnected
        )

        // Download Card
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Download,
            label = "Yuklab olish",
            value = downloadSpeed,
            color = Color(0xFF10B981),
            isActive = isConnected
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    isActive: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                color.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isActive) color.copy(alpha = 0.2f) else color.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = if (isActive) color else color.copy(alpha = 0.5f)
                )
            }

            // Label
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (isActive) 0.7f else 0.5f
                )
            )

            // Value
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) color else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}