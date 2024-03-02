package com.example.appchat.dataclass

import android.net.Uri

data class Message(
    val idUser: String? =null,
    val idReceiver: String? =null,
    val nameReceiver: String? =null,
    val imgUser: String? =null,
    val imgReceiver: String? =null,
    val imgUri1: String? =null,
    val imgUri2: String? =null,
    val imgLink1: String? =null,
    val imgLink2: String? =null,
    val message: String? =null,
    val time :Long?=null
)