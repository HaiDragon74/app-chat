package com.example.appchat.`interface`
import android.net.Uri
import android.util.Log
import com.example.appchat.adapter.FramentAdapter
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ChatRepository {
    interface UserListener {
        fun loadUser(user: List<User>)
        fun errorLoadUser(message: String)
    }
    interface MessageListener {
        fun loadMessage(message: List<Message>)
        fun errorLoadMessage(error: String)
    }
    interface MessageListenerLast {
        fun loadMessageLast(message: List<Message>)
        fun errorLoadMessageLast(error: String)
    }
    interface MessageListenerFist {
        fun loadMessageFist(message: List<Message>)
        fun errorLoadMessageFist(error: String)
    }
    private val dbRef=FirebaseDatabase.getInstance()
    val mutableListUser= mutableListOf<User>()
    val mutableListMessage= mutableListOf<Message>()
    val mutableListMessageReceiver= mutableListOf<Message>()
    val mutableListMessageLast= mutableListOf<Message>()
    val mutableListMessageFist= mutableListOf<Message>()
    val storeRef=FirebaseStorage.getInstance().reference

    fun getUser(listenerUser: UserListener){
        dbRef.getReference("taikhoan").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    mutableListUser.clear()
                    for (data in  snapshot.children){
                        val user=data.getValue(User::class.java)
                        user?.let { mutableListUser.add(it) }
                        Log.d("ahhahhaha",mutableListUser.toString())
                    }
                    listenerUser.loadUser(mutableListUser)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("AAAABBBB",listenerUser.errorLoadUser(error.message).toString())
            }
        })
    }
    fun getMessage(idUserIdRecevier:String,listenerMessage:MessageListener){
        dbRef.getReference("tinnhan").child(idUserIdRecevier)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        mutableListMessage.clear()
                        for (dataMessage in snapshot.children){
                            val message = dataMessage.getValue(Message::class.java)
                            message?.let { mutableListMessage.add(it) }
                            Log.d("SSSSSAAAAA", mutableListMessage.toString())
                        }
                        listenerMessage.loadMessage(mutableListMessage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("AAAABBBB",listenerMessage.errorLoadMessage(error.message).toString())
                }
            })
    }
    fun getMessageReceiver(idRecevierUserId:String,listenerMessageReceiver:MessageListener){
        dbRef.getReference("tinnhan").child(idRecevierUserId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        mutableListMessageReceiver.clear()
                        for (dataMessage in snapshot.children){
                            val message = dataMessage.getValue(Message::class.java)
                            message?.let { mutableListMessageReceiver.add(it) }
                            Log.d("SSSSSAAAAA", mutableListMessageReceiver.toString())
                        }
                        listenerMessageReceiver.loadMessage(mutableListMessageReceiver)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("AAAABBBB",listenerMessageReceiver.errorLoadMessage(error.message).toString())
                }
            })
    }
    fun getMessageLast(idUserIdRecevier:String,listenerMessageLast:MessageListenerLast){
        dbRef.getReference("tinnhan").child(idUserIdRecevier)
            .limitToLast(1)
            .addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()){
                        val message = snapshot.getValue(Message::class.java)
                        message?.let { mutableListMessageLast.add(it) }
                        Log.d("SSSSSAAAAA", mutableListMessageLast.toString())
                        listenerMessageLast.loadMessageLast(mutableListMessageLast)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageUpdater=snapshot.getValue(Message::class.java)
                    messageUpdater?.let {
                        // Tìm vị trí của tin nhắn được cập nhật trong danh sách
                        val index=mutableListMessageLast.indexOfFirst {message->
                            message.idUser==messageUpdater.idUser
                        }
                        if (index!= -1){
                            mutableListMessageLast[index]=messageUpdater
                            listenerMessageLast.loadMessageLast(mutableListMessageLast)
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val message=snapshot.getValue(Message::class.java)
                    message?.let { mutableListMessageLast.remove(it) }
                    listenerMessageLast.loadMessageLast(mutableListMessageLast)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
    fun getMessageLoadFist(idUserIdRecevier:String,listenerMessageFist:MessageListenerFist){
        dbRef.getReference("tinnhan").child(idUserIdRecevier)
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataMessageFist in snapshot.children){
                        val messageFist=dataMessageFist.getValue(Message::class.java)
                        messageFist?.let { mutableListMessageFist.add(it) }
                        Log.d("hsahhdhasdhasd",messageFist?.message.toString())
                    }
                    listenerMessageFist.loadMessageFist(mutableListMessageFist)
                    Log.d("hdahshdsadsa",listenerMessageFist.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

}