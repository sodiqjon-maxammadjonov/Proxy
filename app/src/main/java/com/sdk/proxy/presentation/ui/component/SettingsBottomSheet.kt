package com.sdk.proxy.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sozlamalar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingItem(
                icon = Icons.Default.DarkMode,
                title = "Qorong'i rejim",
                subtitle = "Tizim sozlamalariga moslashish",
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() }
                    )
                }
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            SettingItem(
                icon = Icons.Default.Notifications,
                title = "Bildirishnomalar",
                subtitle = "VPN holati haqida xabarnomalar"
            )

            SettingItem(
                icon = Icons.Default.Security,
                title = "Kill Switch",
                subtitle = "VPN uzilganda internetni o'chirish"
            )

            SettingItem(
                icon = Icons.Default.VpnKey,
                title = "Protokol",
                subtitle = "OpenVPN (Tavsiya etiladi)"
            )

            Divider()

            SettingItem(
                icon = Icons.Default.Info,
                title = "Ilova haqida",
                subtitle = "Versiya 1.0.0"
            )

            SettingItem(
                icon = Icons.Default.PrivacyTip,
                title = "Maxfiylik siyosati",
                subtitle = "Ma'lumotlaringiz qanday ishlatiladi"
            )
        }
    }
}