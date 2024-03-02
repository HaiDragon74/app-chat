package com.example.appchat.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.appchat.R
import com.example.appchat.activity.ChatActivity
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService:FirebaseMessagingService() {
     val CHANNEL_ID="my_notification_channel"

    companion object{
        var sharedPref: SharedPreferences? = null

        var tokeUser:String?
            get(){
            return sharedPref?.getString("token","")
        }
            set(value) {
                sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        tokeUser =token
    }


    //onMessageReceived
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        super.onMessageReceived(message)
        val intent=Intent(this,ChatActivity::class.java)
        val name=message.data["title"]
        Log.d("nananadsadsds",name.toString())
        intent.putExtra("title",name)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId= Random.nextInt()



        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notificationChannel(notificationManager)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent=PendingIntent.getActivity(this,0,intent,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notification=NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId,notification)



    }

    //api 26 tro di moi can
    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChannel(notificationManager: NotificationManager) {

        val channelName="ChannelFirebaseChat"
        val channel=NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {
            description="MY FIREBASE CHAT DESCRIPTION"
            enableLights(true)
            lightColor=Color.WHITE

        }
        notificationManager.createNotificationChannel(channel)
    }
}