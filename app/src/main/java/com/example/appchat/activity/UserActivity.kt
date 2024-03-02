package com.example.appchat.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.adapter.UserAdapter
import com.example.appchat.cosodulieusql.MessageLastSqlHelper
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.databinding.ActivityUserBinding
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.example.appchat.frament.MessageFragment
import com.example.appchat.`interface`.ViewModelUser
import com.example.appchat.notification.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Calendar

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var viewModelUser: ViewModelUser
    private lateinit var userSqlHelper: UserSqlHelper
    private lateinit var messageLastSqlHelper: MessageLastSqlHelper
    private lateinit var dbRef:DatabaseReference
    private var mangUser:MutableList<User> = mutableListOf()
    private var userid:User?=null
    private var messageFragment=MessageFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseService.sharedPref = applicationContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("AAAAFFFF", "failed ${it.exception}")
            return@addOnCompleteListener
            }
            val token=it.result
            Log.d("AAAAFFFF", token.toString())
            if (token!=null)
            {
                FirebaseService.tokeUser=token

            }
            else
                Toast.makeText(this,"NULL",Toast.LENGTH_SHORT).show()
        }
        //LATEINIT
        binding= ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userSqlHelper=UserSqlHelper(this)
        messageLastSqlHelper=MessageLastSqlHelper(this)
        viewModelUser=ViewModelProvider(this)[ViewModelUser::class.java]
        binding.rclUser.layoutManager=LinearLayoutManager(this@UserActivity,LinearLayoutManager.HORIZONTAL,false)
        userAdapter= UserAdapter(this@UserActivity,mangUser)
        //SET IMG USER
        val fbUser=FirebaseAuth.getInstance().currentUser!!
        //LẤY THÔNG TIN IMG THEO ID
        dbRef=FirebaseDatabase.getInstance().getReference("taikhoan").child(fbUser.uid)
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //TRUY CẬP IMG TRONG TAIKHOAN TRÊN FIREBASE
                val getImgtaikhoan=snapshot.getValue(User::class.java)
                // NẾU KHÔNG CÓ GIÁ TRỊ
                if (getImgtaikhoan != null && getImgtaikhoan.img == "")
                    binding.imgUser.setImageResource(R.drawable.avt)
                else
                    Glide.with(this@UserActivity).load(getImgtaikhoan!!.img).into(binding.imgUser)
            }
            // XỮ LÝ LỖI NẾU CÓ
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserActivity,"Thất bại",Toast.LENGTH_SHORT).show()
            }
        })
        //ADD USER
        viewModelUser.loadUser()
        getUser()
        //CLICKEXIT(TRỞ VỀ)
/*        binding.imgExit.setOnClickListener {
            ClickExit()
        }*/
        //CLICK IMGUser
        binding.imgUser.setOnClickListener {
            editProfile()
        }
        val list= messageLastSqlHelper.readListMessageLastSql()
        Log.d("hahahahahah",list.toString())
        val listmesage= userSqlHelper.getAllUsers()
        Log.d("hahahsssahahah",listmesage.toString())
        val messageSqlHelper=MessageSqlHelper(this)
        val listmesage1111= messageSqlHelper.readListMessageSql()
        Log.d("hahahsss00ahahah",listmesage1111.toString())

        openFramentMesage()
        connected()
    }
    private fun connected() {
        val user= dbRef.child("connected")
        val connectedRef=FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected=snapshot.getValue(Boolean::class.java)?:false
                if (connected){
                    user.setValue(true)
                    user.onDisconnect().cancel()
                    user.onDisconnect().setValue(false)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun openFramentMesage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frmLayout,messageFragment)
            commit()
        }
    }


    //CHUYỂN MÀN HÌNH
    private fun editProfile() {
        val intent=Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }


    private fun getUser() {
        // LẤY ĐỐI TƯỢNG FIREBASEUSER ĐANG ĐĂNG NHẬP
        val fbUser=FirebaseAuth.getInstance().currentUser!!
        // TRUY VẤN "TAIKHOAN" TRONG FIREBASE REALTIME DATABASE
        val idUser=fbUser.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/${idUser}")
        val listUser=userSqlHelper.getAllUsers()
        listUser.forEach {user->
            mangUser.add(user)
        }
        binding.rclUser.adapter=userAdapter
        userAdapter.notifyDataSetChanged()
        viewModelUser.ldtGetUser().observe(this, Observer {
            userSqlHelper.deleteUser()
            it.forEach {user->
                if (!user!!.id.equals(fbUser.uid))
                {
                    userSqlHelper.addUser(user)
                }
            }
            binding.rclUser.adapter=userAdapter
            userAdapter.notifyDataSetChanged()
        })
        userAdapter.click={user->
            //TRUYỀN DỮ LIỆU SANG USERACTIVITY
            val intent=Intent(this@UserActivity,ChatActivity::class.java)
            intent.putExtra("ID","${user.id}")
            intent.putExtra("NAME","${user.name}")
            intent.putExtra("IMG","${user.img}")
            startActivity(intent)
            Log.d("GGGGGGGG",user.name.toString())
        }
    }
}