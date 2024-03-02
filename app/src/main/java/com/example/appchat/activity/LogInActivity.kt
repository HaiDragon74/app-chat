package com.example.appchat.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.appchat.R
import com.example.appchat.cosodulieusql.MessageLastSqlHelper
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.databinding.ActivityLogInBinding
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.example.appchat.`interface`.ViewModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var userSqlHelper: UserSqlHelper
    private lateinit var viewModelUser: ViewModelUser
    private lateinit var progressDialog:ProgressDialog
    private lateinit var messageLastSqlHelper:MessageLastSqlHelper
    private var dataMutableListUser:MutableList<User> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //lateinit
        //LẤY ĐỐI TƯỢNG XÁC THỰC CỦA FIREBASE
        fbAuth = FirebaseAuth.getInstance()
        userSqlHelper=UserSqlHelper(this)
        progressDialog=ProgressDialog(this)
        messageLastSqlHelper= MessageLastSqlHelper(this)
        viewModelUser=ViewModelProvider(this)[ViewModelUser::class.java]
        //TU DONG LOGIN
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CLICK VÀO HIỆN MK
        binding.btnOnPass.setOnClickListener {
            clickOnPass()
        }
        //XỮ LÝ SỰ KIỆN KHI CLICK VÀO BUTTOM ĐĂNG KÍ
        binding.clickSignIn.setOnClickListener {
            clickSignIn()
        }
        //KIỂM TRA MK CO DỮ LIỆU CHƯA
        binding.btnLogIn.setOnClickListener {
            logIn()
        }

    }
    private fun loadDataUser() {
        viewModelUser.loadUser()
        viewModelUser.ldtGetUser().observe(this, Observer {
            userSqlHelper.deleteUser()
            dataMutableListUser.clear()
            it.forEach {user->
                if (user.id==user.id){
                    if (!user!!.id.equals(fbAuth.currentUser?.uid))
                    {
                        userSqlHelper.addUser(user)
                        dataMutableListUser.add(user)
                    }
                }
            }
            getListMessageLast()
        })

    }

    private fun getListMessageLast() {
        dataMutableListUser.removeIf { user-> user.id==fbAuth.currentUser?.uid }
        dataMutableListUser.forEach {user ->
            Log.d("ahahahahssaaa",user.toString())
            viewModelUser.loadMessageFist("${fbAuth.currentUser?.uid}-${user.id}")
        }
        viewModelUser.ldtGetMessageFist().observe(this, Observer {listMessage->
            Log.d("aagagaggaa",listMessage.toString())
            listMessage.forEach {message->
                Log.d("ajajajajsasja",message.toString())
                messageLastSqlHelper.deleteById(message)
                messageLastSqlHelper.addMessageLast(message)
            }
            val intent=Intent(this,UserActivity::class.java)
            progressDialog.dismiss()
            startActivity(intent)
            finish()
        })
    }

    private fun logIn() {
        //KIỂM TRA XEM EDT CÓ TRỐNG KHÔNG
        if (binding.edtAccounts.text.isEmpty()) {
            binding.edtAccounts.error = "chưa nhật tài khoản"
            return
        } else if (binding.edtPassword.text.isEmpty()) {
            binding.edtPassword.error = "chưa nhập mật khẩu"
            return
        }
        //LẤY DỮ LIỆU EDT
        val edtEmail = binding.edtAccounts.text.toString()
        val edtMatkhau = binding.edtPassword.text.toString()
        //KIỂM TRA DĂNG NHẬP BẰNG signInWithEmailAndPassword
        fbAuth.signInWithEmailAndPassword(edtEmail, edtMatkhau)
            .addOnCompleteListener(this) { Task ->
                if (Task.isSuccessful) {
                    progressDialog.setTitle("Đang tải")
                    progressDialog.show()
                    loadDataUser()
                    Log.d("DANGNHAP", "DANG NHAP THANH CONG")
                } else {
                    // NẾU ĐĂNG NHẬP THẤT BẠI
                    Log.d("DANGNHAP", "${Task.exception}")
                    // CHO HIEN LEN DIALOG NEN NHAP SAI TAI KHOAN
                    val dialog = AlertDialog.Builder(this)
                    dialog.apply {
                        setTitle("Đăng nhập")
                        setMessage("Email hoặc mật khẩu không chính xác")
                        setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                        }
                    }.show()
                }

            }
    }


    private fun clickOnPass() {
        val edtend = binding.edtPassword.text.length // LẤY ĐỘ DÀI VĂN BẢN
        // KIỂM TRA NẾU MẬT KHẨU ĐANG ĐƯỢC HIỂN THỊ
        if (binding.edtPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // ẨN MẬT KHẨU
            binding.edtPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // CẬP NHẬT HÌNH ẢNH
            binding.btnOnPass.setImageResource(R.drawable.hienmk)

        } else {
            // HIỂN THỊ MẬT KHẨU
            binding.edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // CẬP NHẬT HÌNH ẢNH
            binding.btnOnPass.setImageResource(R.drawable.anmk)
        }
        binding.edtPassword.setSelection(edtend) // DI CHUYỂN CON TRỎ VỀ CUỐI VĂN BẢN
    }
    //CHUYỂN MAN HINH
    private fun clickSignIn() {
        // chuyển màn hình
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()

    }
}


//XOA TK
/*
val user = FirebaseAuth.getInstance().currentUser
user?.delete()
?.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        // Tài khoản người dùng đã được xóa thành công
        // Tiếp theo, bạn có thể tạo tài khoản mới với cùng một địa chỉ email
    } else {
        // Xử lý lỗi khi xóa tài khoản người dùng
    }
}*/
//GIU LINK RESET MK
/*val auth = FirebaseAuth.getInstance()
val emailAddress = "user@example.com"

auth.sendPasswordResetEmail(emailAddress)
.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        // Email đặt lại mật khẩu đã được gửi thành công
        // Hướng dẫn người dùng kiểm tra email để đặt lại mật khẩu
    } else {
        // Xử lý lỗi khi gửi email đặt lại mật khẩu
    }
}*/
