package com.example.appchat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val fbUser=FirebaseAuth.getInstance().currentUser
        if (fbUser?.uid!=null)
        {
            //TIẾN HÀNH ĐĂNG NHẬP
            val intent= Intent(this,UserActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent= Intent(this,LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}