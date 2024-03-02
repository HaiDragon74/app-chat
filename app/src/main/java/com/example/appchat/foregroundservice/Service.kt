package com.example.appchat.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.appchat.R

class Service: Service() {
    val application=Application()
    private var serviceRun=false
    private var name:String?=null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       val hilo=intent?.getStringExtra("hilo")
        if (hilo!=null){
            sendNotification(hilo)
        }
        return START_STICKY

    }

    private fun sendNotification(hilo: String) {

        val notificationCompat=NotificationCompat.Builder(this,application.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(hilo)
            .setContentText(hilo)
            .build()
        startForeground(1,notificationCompat)
    }
}