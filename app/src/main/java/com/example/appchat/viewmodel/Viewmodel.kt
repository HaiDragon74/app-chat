package com.example.appchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appchat.dataclass.ImgInsert

class Viewmodel:ViewModel() {
    private lateinit var listImgInsert: MutableLiveData<String>
    fun addImgtoRcl(imgInsert: String){
        listImgInsert.value=imgInsert
    }
    fun obsImgtoRcl():LiveData<String>{
        return listImgInsert
    }
}