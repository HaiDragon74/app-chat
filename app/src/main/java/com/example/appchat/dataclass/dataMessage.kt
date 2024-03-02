package com.example.appchat.dataclass

import com.google.firebase.iid.FirebaseInstanceIdReceiver

data class dataMessage(
    val idUserReceiver:String?=null,
    val message: Message
) {
}