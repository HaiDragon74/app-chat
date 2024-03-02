package com.example.appchat.dataclass

data class Call(
    val target: String?=null,
    val sender: String?=null,
    val data:String?=null,
    val type:DataModelType?=null

) {

}
