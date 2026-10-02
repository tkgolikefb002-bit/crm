package com.example.auto800best

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class KeepAliveService : Service() {

    private val CHANNEL_ID = "Auto800BestChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Tạo thông báo bắt buộc để Android hiểu đây là Foreground Service (không bị kill ngầm)
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Auto 800Best đang chạy ngầm")
            .setContentText("Đang giám sát và chờ dữ liệu mới...")
            .setSmallIcon(R.mipmap.ic_launcher) // Thay bằng icon của bạn
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Duy trì dịch vụ chạy liên tục ngay cả khi app bị vuốt khỏi Recent Apps
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Auto 800Best Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
