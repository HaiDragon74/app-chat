package com.example.appchat.`interface`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User

class ViewModelUser():ViewModel() {
    private var mutableLiveDataUser=MutableLiveData<List<User>>()
    private var mutableLiveDataMessage=MutableLiveData<List<Message>>()
    private var mutableLiveDataMessageReceiver=MutableLiveData<List<Message>>()
    private var mutableLiveDataMessageLast=MutableLiveData<List<Message>>()
    private var mutableLiveDataMessageFist=MutableLiveData<List<Message>>()
    private var chatRepository=ChatRepository()
    fun loadUser(){
        chatRepository.getUser(object : ChatRepository.UserListener{
            override fun loadUser(user: List<User>) {
                mutableLiveDataUser.value=user

            }

            override fun errorLoadUser(error: String) {
                TODO("Not yet implemented")
            }
        })
    }
    fun loadMessage(idUserIdReceiver:String){
        chatRepository.getMessage(idUserIdReceiver,object : ChatRepository.MessageListener{
            override fun loadMessage(message: List<Message>) {
                mutableLiveDataMessage.value=message
                Log.d("HAHAHAHA",mutableLiveDataMessage.toString())
            }

            override fun errorLoadMessage(error: String) {
                TODO("Not yet implemented")
            }
        })
    }
    fun loadMessageReceiver(idReceiverUserId:String){
        chatRepository.getMessage(idReceiverUserId,object : ChatRepository.MessageListener{
            override fun loadMessage(message: List<Message>) {
                mutableLiveDataMessageReceiver.value=message
                Log.d("HAHAHAHA",mutableLiveDataMessageReceiver.toString())
            }

            override fun errorLoadMessage(error: String) {
                TODO("Not yet implemented")
            }
        })
    }
    fun loadMessageLast(idUserIdReceiver:String){
        chatRepository.getMessageLast(idUserIdReceiver,object : ChatRepository.MessageListenerLast{
            override fun loadMessageLast(message: List<Message>) {
                mutableLiveDataMessageLast.value=message
                Log.d("HAHAHAHA",mutableLiveDataMessageLast.toString())
            }

            override fun errorLoadMessageLast(error: String) {
                TODO("Not yet implemented")
            }
        })
    }
    fun loadMessageFist(idUserIdReceiver:String){
        chatRepository.getMessageLoadFist(idUserIdReceiver,object :ChatRepository.MessageListenerFist{
            override fun loadMessageFist(message: List<Message>) {
                mutableLiveDataMessageFist.value=message
            }

            override fun errorLoadMessageFist(error: String) {
                TODO("Not yet implemented")
            }
        })
    }



    fun ldtGetUser():LiveData<List<User>>{
        return mutableLiveDataUser
    }
    fun ldtGetMessage():LiveData<List<Message>>{
        return mutableLiveDataMessage
    }
    fun ldtGetMessageLast():LiveData<List<Message>>{
        return mutableLiveDataMessageLast
    }
    fun ldtGetMessageFist():LiveData<List<Message>>{
        return mutableLiveDataMessageFist
    }
    fun ldtGetMessageReceiver():LiveData<List<Message>>{
        return mutableLiveDataMessageReceiver
    }
}
