package com.example.appchat.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface RetroifitInterface {
    companion object{
        private val retroifit= Retrofit.Builder()
            .baseUrl(Constamts.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api = retroifit.create(NotificationApi::class.java)
    }
}