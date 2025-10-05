package com.sdk.proxy.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.sdk.proxy.R

@Composable
fun ConnectionStatusCard(
    isConnected: Boolean,
    isLoading: Boolean = false,
    onToggleConnection: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Pulse animatsiyasi faqat ulanganda
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Loading uchun aylanish animatsiyasi
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shield Icon with Loading State
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLoading -> Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF2563EB)
                                )
                            )
                            isConnected -> Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF059669)
                                )
                            )
                            else -> Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6B7280),
                                    Color(0xFF4B5563)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    // Loading animatsiyasi
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )
                } else {
                    // Shield icon
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                }
            }

            // Status Text
            Text(
                text = when {
                    isLoading && isConnected -> "Uzilmoqda..."
                    isLoading -> "Ulanmoqda..."
                    isConnected -> "Himoyalangan"
                    else -> "Himoyalanmagan"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = when {
                    isLoading && isConnected -> "Iltimos kuting..."
                    isLoading -> "Server bilan bog'lanilmoqda..."
                    isConnected -> "Internet aloqangiz xavfsiz"
                    else -> "VPN orqali ulanishingiz kerak"
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            // Connect Button
            Button(
                onClick = onToggleConnection,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading, // Loading paytida disable qilish
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            ) {
                if (!isLoading) {
                    Icon(
                        painter = painterResource(R.drawable.ic_power),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = when {
                        isLoading -> "Kutilmoqda..."
                        isConnected -> "Uzish"
                        else -> "Ulash"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}