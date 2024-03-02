package com.example.appchat.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

fun ImageView.loadImage(url: String?){
    Glide.with(context).load(url).into(this)
}

fun ImageView.loadImage(file: Uri?){
    Glide.with(context).load(file).into(this)
}