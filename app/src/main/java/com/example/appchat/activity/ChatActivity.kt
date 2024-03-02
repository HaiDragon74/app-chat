package com.example.appchat.activity

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.adapter.ImgInsertAdapter
import com.example.appchat.adapter.MessageAdapter
import com.example.appchat.cosodulieusql.MessageLastSqlHelper
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.databinding.ActivityChatBinding
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.example.appchat.notification.PushNotification
import com.example.appchat.notification.NotificationData
import com.example.appchat.notification.RetroifitInterface
import com.example.appchat.`interface`.ViewModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var idReceiver: String = ""
    private var nameReceiver: String = ""
    private var imgReceiver: String = ""
    private var dataUser: User?=null
    private lateinit var fbUser: FirebaseUser
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbImgInsertRef: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var storeRef:StorageReference
    private lateinit var fbStorage: FirebaseStorage
    private lateinit var viewModelUser: ViewModelUser
    private lateinit var imgInsertAdapter: ImgInsertAdapter
    private lateinit var messageSqlHelper: MessageSqlHelper
    private lateinit var userSqlHelper: UserSqlHelper
    private var mutableListMessage: MutableList<Message> = mutableListOf()
    var topic = ""
    private var clipData:ClipData?=null
    private var uri:Uri?=null
    private var conversationId:String=""
    private var REQUEST_PICK=2024
    private var linkImgInsert:MutableList<Uri> = mutableListOf()
    private var selectedImage= mutableListOf<Uri>()
    private var idMessage:String= ""

    val prermissons= arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO)
    val requestCode=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //lateinit var
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fbUser = FirebaseAuth.getInstance().currentUser!!
        dbRef = FirebaseDatabase.getInstance().getReference("tinnhan")
        dbImgInsertRef = FirebaseDatabase.getInstance().getReference("imgInsert")
        dbRefUser = FirebaseDatabase.getInstance().getReference("taikhoan").child(fbUser.uid)
        viewModelUser=ViewModelProvider(this)[ViewModelUser::class.java]
        imgInsertAdapter= ImgInsertAdapter(this,contentResolver)
        messageSqlHelper=MessageSqlHelper(this)
        userSqlHelper= UserSqlHelper(this)
        fbStorage=FirebaseStorage.getInstance()
        storeRef=fbStorage.reference

        //NHẬN DỮ LIỆU TỪ USERACTIVITY
        getdata()
        dataUser()
        //DAY TIN NHAN LEN FIREBASE
        binding.rclInsert.layoutManager = LinearLayoutManager(this@ChatActivity,LinearLayoutManager.HORIZONTAL, false)
        binding.rclInsert.adapter=imgInsertAdapter
        binding.btnInsert.setOnClickListener {
            openLibraryAndroid()
        }
        binding.btnAdd.setOnClickListener {
            meessageFirebase()
            dataMessageLast()
        }
        messageAdapter = MessageAdapter(this,mutableListMessage)
        binding.rclMessage.layoutManager = LinearLayoutManager(this@ChatActivity,LinearLayoutManager.VERTICAL, false)
        binding.rclMessage.adapter=messageAdapter
        conversationId = if (fbUser.uid < idReceiver) {
            "${fbUser.uid}-$idReceiver"
        } else {
            "$idReceiver-${fbUser.uid}"
        }
        viewModelUser.loadMessage(conversationId)
        Log.d("VIEWMODELUSERLOADMESSAGE",conversationId)
        adapterMessage()

/*        putImgInsertFirebase()*/
        //binding.imgbtnCall.setOnClickListener {
            callRequest()
        //}
        activeStatus()

    }

    private fun activeStatus() {
        val dbConnecRef=FirebaseDatabase.getInstance().getReference("taikhoan").child(idReceiver)
        Log.d("dsadsadsa",idReceiver)
        dbConnecRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var connected=snapshot.child("connected").getValue(Boolean::class.java)
                    Log.d("dsajsjsjasas",connected.toString())
                    if (connected == true){
                        binding.txtConnected.text="Online"
                        binding.txtConnected.setTextColor(Color.RED)
                    }else
                        binding.txtConnected.text="Offline"
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun dataMessageLast() {
        viewModelUser.loadMessageLast(conversationId)
        val messageLastSqlHelper=MessageLastSqlHelper(this)
        viewModelUser.ldtGetMessageLast().observe(this, Observer {
            it.forEach {message->
                messageLastSqlHelper.deleteById(message)
                messageLastSqlHelper.addMessageLast(message)
            }
        })
    }
    private fun callRequest() {
        if (isPermissionGranted()){
            askPermissions()
        }
        callme()

    }

    private fun callme() {
        binding.imgbtnCall.setOnClickListener {
            val intent=Intent(this,CallActivity::class.java)
            startActivity(intent)

        }
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this,prermissons,requestCode)
    }

    private fun isPermissionGranted(): Boolean {
        prermissons.forEach {
            if (ActivityCompat.checkSelfPermission(this,it)!=PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun adapterMessage() {
        val listMessage= messageSqlHelper.readListMessageSql()
        Log.d("HAHAHHAAA",listMessage.toString())
        mutableListMessage.clear()
        for (tabMessage in listMessage) {
            if ("${fbUser.uid}-$idReceiver"=="${tabMessage.idUser}-${tabMessage.idReceiver}") {
                mutableListMessage.add(tabMessage)
            }
        }
        binding.rclMessage.adapter=messageAdapter
        messageAdapter.notifyDataSetChanged()
        binding.rclMessage.scrollToPosition(messageAdapter.itemCount-1)
        viewModelUser.ldtGetMessage().observe(this, Observer { it ->
            mutableListMessage.clear()
            it.forEach { message ->
                    messageSqlHelper.deleteMessageByTime(message)
                    messageSqlHelper.addMessage(message)
                    mutableListMessage.add(message)
            }
            binding.rclMessage.adapter=messageAdapter
            messageAdapter.notifyDataSetChanged()
            binding.rclMessage.scrollToPosition(messageAdapter.itemCount - 1)
        })
    }
    private fun meessageFirebase() {
        // TẠO MỘT ID MỚI CHO TIN NHẮN
        val id = fbUser.uid
        Log.d("AAAAAAA", id)
        // LẤY NỘI DUNG TIN NHẮN TỪ EDITTEXT
        var edtMeessage = binding.edtMessage.text.toString()
        val imgUser=dataUser!!.img
        var imgUri=uri
        var calendar =Calendar.getInstance()
        val time=calendar.timeInMillis
        idMessage=dbRef.push().key!!
        // KIỂM TRA XEM NỘI DUNG TIN NHẮN CÓ TRỐNG KHÔNG
        if (selectedImage.isNotEmpty())
        {
            val imgUri1= selectedImage[0].toString()
            val imgUri2 =
                if (selectedImage.size>=2)
                    selectedImage[1].toString()
                else
                    ""
            val storageRef1= storeRef.child("imgInsert/${dbRef.push().key!!}")
            val storageRef2= storeRef.child("imgInsert/${dbRef.push().key!!}")
            val messageTo = Message(id,idReceiver,nameReceiver,imgUser,imgReceiver,imgUri1,imgUri2,"","", edtMeessage,time)
            dbRef.child(conversationId).child(idMessage).setValue(messageTo)
            imgInsertAdapter.clear()
            messageAdapter.notifyDataSetChanged()
            val message=
            if (edtMeessage.isNotEmpty()&&binding.rclInsert.isNotEmpty()){
                messageTo.copy()
            }else {
                messageTo.copy(message = "")
            }
            // TẠO MỘT ĐỐI TƯỢNG TINNHAN
            storageRef1.putFile(imgUri1.toUri()).addOnSuccessListener {
                storageRef1.downloadUrl.addOnSuccessListener {link1->
                    val messageLink1=message.copy(imgLink1 = link1.toString())
                    dbRef.child(conversationId)
                        .child(idMessage).setValue(messageLink1)
                        .addOnSuccessListener { updater() }
                    storageRef2.putFile(imgUri2.toUri()).addOnSuccessListener {
                        storageRef2.downloadUrl.addOnSuccessListener {link2->
                            val messageLink2=message.copy(imgLink1 =link1.toString() ,imgLink2 = link2.toString())
                            dbRef.child(conversationId)
                                .child(idMessage)
                                .setValue(messageLink2)
                                .addOnSuccessListener { updater() }
                        }
                    }
                }

            }
            imgInsertAdapter.clear()

        }else {
            val message =
            if (edtMeessage.isNotEmpty()&&binding.rclInsert.isEmpty()) {
                 Message(id, idReceiver,nameReceiver, imgUser, imgReceiver, "", "", "", "", edtMeessage,time)
                //ADD DỮ LIỆU LÊN FIRABASE
            }else
                 Message(id, idReceiver, nameReceiver, imgUser, imgReceiver, "", "", "", "","",time)

            dbRef.child(conversationId).child(idMessage).setValue(message)
                .addOnSuccessListener { updater() }
            imgInsertAdapter.clear()
        }
        //NOTIFICATION
        topic = "/topics/$idReceiver"
        PushNotification(NotificationData(idReceiver,dataUser!!.name!!, edtMeessage), topic).also {
            sendNotification(it)
        }

            binding.edtMessage.setText("")
    }
    private fun openLibraryAndroid() {
        val intent=Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.type="image/*"
        startActivityForResult(Intent.createChooser(intent,"selecter pick"),REQUEST_PICK)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==REQUEST_PICK&&resultCode== RESULT_OK){
            clipData=data!!.clipData
            if (clipData!=null)
            {
                for (i in 0 until clipData!!.itemCount)
                {
                    uri=clipData!!.getItemAt(i).uri
                    selectedImage.add(uri!!)
                }

            }
        }
        imgInsertAdapter.getAdapter(this,selectedImage)
    }
    private fun dataUser() {
        dbRefUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)
                dataUser=user
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    //LẤY DỮ LIỆU TỪ USERACTIVITY
    private fun getdata() {
        val intent = intent/*        // LẤY CÁC DỮ LIỆU*/
        idReceiver = intent.getStringExtra("ID").toString()
        nameReceiver = intent.getStringExtra("NAME").toString()
        imgReceiver = intent.getStringExtra("IMG")
            .toString()/*        // HIỂN THỊ TÊN NGƯỜI DÙNG TRÊN TEXTVIEW*/
        binding.txtUser.text=nameReceiver
        if (binding.txtUser.text=="null")
        {
            val titel=intent.getStringExtra("title")
            binding.txtUser.text=titel
        }
        /*// KIỂM TRA XEM CÓ HÌNH ẢNH NGƯỜI DÙNG HAY KHÔNG*/
        if (imgReceiver == "") {
            binding.imgUser.setImageResource(R.drawable.avt)
        } else
            Glide.with(this).load(imgReceiver).into(binding.imgUser)
    }
    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetroifitInterface.api.posNotification(notification)
                if (response.isSuccessful) {

                    val responseBody = response.body()
                    if (responseBody != null) {
                        withContext(Dispatchers.Main) {
                            /*Toast.makeText(this@ChatActivity, Gson().toJson(responseBody), Toast.LENGTH_SHORT).show()*/ //null
                        }
                    }
                    else {
                        Log.e("AAAAAAAAAAAAAA", "Phần thân của phản hồi là null")
                    }
                }
                else {
                    if (response.code() == 404) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ChatActivity, "URL not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()
                        if (errorBody != null) {
                            val errorBodyString = errorBody.string()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ChatActivity, errorBodyString, Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Log.e("SSSSSSSSSSSSSSS", "Phần thân lỗi là null")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("wwwwwwwwwwww", e.message ?: "Lỗi không xác định")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatActivity, e.message ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private fun updater(){
        val hashMap=HashMap<String,String>()
        hashMap.put("trangthai","dagui")
        dbRef.child(conversationId).child(idMessage).updateChildren(hashMap as Map<String, Any>)
    }
}