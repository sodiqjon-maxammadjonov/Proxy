package com.sdk.proxy.data.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.sdk.proxy.MainActivity
import com.sdk.proxy.R
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

@SuppressLint("VpnServicePolicy")
class ProxyVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false

    // Traffic statistikasi
    private var uploadBytes = 0L
    private var downloadBytes = 0L
    private var lastUpdateTime = System.currentTimeMillis()

    companion object {
        const val ACTION_CONNECT = "com.sdk.proxy.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.sdk.proxy.ACTION_DISCONNECT"
        const val EXTRA_SERVER_ADDRESS = "server_address"
        const val EXTRA_SERVER_PORT = "server_port"

        const val NOTIFICATION_CHANNEL_ID = "vpn_channel"
        const val NOTIFICATION_ID = 1

        var uploadSpeed = 0L
        var downloadSpeed = 0L
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val serverAddress = intent.getStringExtra(EXTRA_SERVER_ADDRESS) ?: "8.8.8.8"
                val serverPort = intent.getIntExtra(EXTRA_SERVER_PORT, 53)
                startVpn(serverAddress, serverPort)
            }
            ACTION_DISCONNECT -> {
                stopVpn()
            }
        }
        return START_STICKY
    }

    private fun startVpn(serverAddress: String, serverPort: Int) {
        if (isRunning) return

        // VPN ni sozlash
        val builder = Builder()
            .setSession("SecureVPN")
            .addAddress("10.0.0.2", 24) // Virtual IP manzil
            .addRoute("0.0.0.0", 0) // Barcha traffic ni yo'naltirish
            .addDnsServer("8.8.8.8") // Google DNS
            .addDnsServer("8.8.4.4")
            .setMtu(1500)

        // VPN interfeys ni yaratish
        vpnInterface = builder.establish()

        if (vpnInterface == null) {
            stopSelf()
            return
        }

        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification("Ulangan", true))

        // VPN traffic ni boshqarish
        serviceScope.launch {
            handleVpnTraffic(serverAddress, serverPort)
        }

        // Speed ni hisoblash
        serviceScope.launch {
            calculateSpeed()
        }
    }

    private suspend fun handleVpnTraffic(serverAddress: String, serverPort: Int) {
        withContext(Dispatchers.IO) {
            try {
                val vpnInput = FileInputStream(vpnInterface!!.fileDescriptor)
                val vpnOutput = FileOutputStream(vpnInterface!!.fileDescriptor)

                // UDP kanal yaratish (haqiqiy VPN da bu murakkabroq bo'ladi)
                val channel = DatagramChannel.open()
                channel.connect(InetSocketAddress(serverAddress, serverPort))
                channel.configureBlocking(false)

                val buffer = ByteBuffer.allocate(32767)

                while (isRunning && !Thread.currentThread().isInterrupted) {
                    // VPN dan paketlarni o'qish
                    buffer.clear()
                    val readBytes = vpnInput.read(buffer.array())

                    if (readBytes > 0) {
                        uploadBytes += readBytes

                        // Bu yerda paketni serverga yuborish kerak
                        // Haqiqiy implementatsiyada paket parsing va forwarding bo'ladi
                        buffer.limit(readBytes)
                        channel.write(buffer)
                    }

                    // Serverdan javob olish
                    buffer.clear()
                    val receivedBytes = channel.read(buffer)

                    if (receivedBytes > 0) {
                        downloadBytes += receivedBytes
                        buffer.flip()
                        vpnOutput.write(buffer.array(), 0, receivedBytes)
                    }

                    // CPU ni yuklamaslik uchun kichik delay
                    delay(1)
                }

                channel.close()
                vpnInput.close()
                vpnOutput.close()

            } catch (e: Exception) {
                e.printStackTrace()
                stopVpn()
            }
        }
    }

    private suspend fun calculateSpeed() {
        while (isRunning) {
            delay(1000) // Har soniyada yangilash

            val currentTime = System.currentTimeMillis()
            val timeDiff = (currentTime - lastUpdateTime) / 1000.0

            if (timeDiff > 0) {
                uploadSpeed = (uploadBytes / timeDiff).toLong()
                downloadSpeed = (downloadBytes / timeDiff).toLong()

                // Reset statistika
                uploadBytes = 0
                downloadBytes = 0
                lastUpdateTime = currentTime

                // Notificationni yangilash
                val notification = createNotification(
                    "Upload: ${formatSpeed(uploadSpeed)} | Download: ${formatSpeed(downloadSpeed)}",
                    true
                )
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond < 1024 -> "$bytesPerSecond B/s"
            bytesPerSecond < 1024 * 1024 -> "${bytesPerSecond / 1024} KB/s"
            else -> "${bytesPerSecond / (1024 * 1024)} MB/s"
        }
    }

    private fun stopVpn() {
        isRunning = false
        serviceScope.cancel()
        serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        vpnInterface?.close()
        vpnInterface = null

        uploadSpeed = 0
        downloadSpeed = 0
        uploadBytes = 0
        downloadBytes = 0

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "VPN ulanish holati"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String, isConnected: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, ProxyVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }

        val disconnectPendingIntent = PendingIntent.getService(
            this,
            0,
            disconnectIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("SecureVPN")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Bu icon ni o'zgartiring
            .setContentIntent(pendingIntent)
            .setOngoing(isConnected)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Uzish",
                disconnectPendingIntent
            )
            .build()
    }
}