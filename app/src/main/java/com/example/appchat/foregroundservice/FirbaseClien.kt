package com.example.appchat.foregroundservice

import android.util.Log
import android.widget.Toast
import com.example.appchat.dataclass.Call
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.util.Objects

class FirbaseClien() {
    private val gson=Gson()
    private val dbRef=FirebaseDatabase.getInstance().getReference()
    var curren:String=""
    private val LATEST_EVENTER="latest_aventer"

    fun login(nam:String,successCall:CallBlack){
        dbRef.child(nam).setValue("").addOnSuccessListener { task->
            curren=nam
            successCall.success()
        }
    }
    fun sendMessage(call:Call,errorCall: ErrorCall){
        dbRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    call.target?.let {
                        dbRef.child(it).child(LATEST_EVENTER)
                            .setValue(gson.toJson(call))
                    }
                }else
                    errorCall.errorCall()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    fun obsEventer(newCallBlack: NewCallBlack){
        dbRef.child(curren).child(LATEST_EVENTER).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data=Objects.requireNonNull(snapshot.value.toString())
                    val mode=gson.fromJson(data,Call::class.java)
                    newCallBlack.newReceiver(mode)
                }
                catch (err:Exception){
                    Log.d("AAABBB",err.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}