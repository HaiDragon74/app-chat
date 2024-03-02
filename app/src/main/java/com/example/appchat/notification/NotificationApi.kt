package com.example.appchat.notification

import com.example.appchat.notification.Constamts.Companion.CONTEN_TYPE
import com.example.appchat.notification.Constamts.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: Key =$SERVER_KEY","Content_Type:$CONTEN_TYPE")
    @POST("/fcm/send")
    suspend fun posNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}