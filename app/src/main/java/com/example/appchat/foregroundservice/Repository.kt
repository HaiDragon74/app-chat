package com.example.appchat.foregroundservice

import android.util.Log
import com.example.appchat.dataclass.Call
import com.example.appchat.dataclass.DataModelType

class Repository {
    companion object{
        private var instance=Repository()
        fun getInstance():Repository{
            if (instance==null){
                instance= Repository()
            }
            return instance
        }
    }
    private var currentName:String=""
    fun updaterCurrname(name: String){
        this.currentName=name
    }
    private val firebaseClien=FirbaseClien()

    fun login(name:String,successCall:CallBlack){
        firebaseClien.login(name,object :CallBlack{
            override fun success() {
                updaterCurrname(name)
                successCall.success()
            }
        })
    }
    fun sendCall(target: String,errorCall: ErrorCall){
        firebaseClien.sendMessage(Call(target,currentName,null,DataModelType.StarCall),errorCall)

    }
    fun subEventer(newCallBlack: NewCallBlack){
        firebaseClien.obsEventer(object :NewCallBlack{
            override fun newReceiver(call: Call) {
                when(call.type){
                    DataModelType.Offer->Log.d("hishih","hihihi")
                    DataModelType.Answer->Log.d("hisshih","hihihi")
                    DataModelType.IceCandidate->Log.d("hidddhih","hihihi")
                    DataModelType.StarCall->newCallBlack.newReceiver(call)
                    else -> {}
                }
            }
        })
    }

}