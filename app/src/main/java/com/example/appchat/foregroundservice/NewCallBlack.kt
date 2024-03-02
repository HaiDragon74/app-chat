package com.example.appchat.foregroundservice

import com.example.appchat.dataclass.Call

interface NewCallBlack {
    fun newReceiver(call:Call)
}