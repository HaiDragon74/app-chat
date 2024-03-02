package com.example.appchat.activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.appchat.R
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.databinding.ActivitySignInBinding
import com.example.appchat.dataclass.User
import com.example.appchat.`interface`.ViewModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var fbAuth: FirebaseAuth
    private lateinit var viewModelUser: ViewModelUser
    private lateinit var userSqlHelper:UserSqlHelper
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModelUser=ViewModelProvider(this)[ViewModelUser::class.java]
        userSqlHelper= UserSqlHelper(this)
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Đang tải")
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //lateinit var
        //TẠO FIRABASE TÊN TAIKHOAN
        dbRef = FirebaseDatabase.getInstance().getReference("taikhoan")
        // LẤY THÔNG TIN NGƯỜI DÙNG
        fbAuth = FirebaseAuth.getInstance()

        //CLICK HIEN MAT KHAU
        binding.btnHienMk.setOnClickListener {
            clickHienMatKhau()
        }
        // CLICK LAI MAT KHAU
        binding.btnLaiHienMk.setOnClickListener {
            clickLaiMatKhau()
        }


        //ADD SQLDATABASE
        binding.btnDangki.setOnClickListener {
            addTaiKhoanLenFỉebase()
        }
    }

    private fun clickLaiMatKhau() {
        val edtLaiEnd = binding.edtLaiMatKhau.text.length // Lấy độ dài văn bản
        // Kiểm tra nếu mật khẩu đang được hiển thị
        if (binding.edtLaiMatKhau.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Ẩn mật khẩu
            binding.edtLaiMatKhau.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // Cập nhật hình ảnh
            binding.btnHienMk.setImageResource(R.drawable.hienmk)
        } else {
            // Hiển thị mật khẩu
            binding.edtLaiMatKhau.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Cập nhật hình ảnh
            binding.btnHienMk.setImageResource(R.drawable.anmk)
        }
        binding.edtLaiMatKhau.setSelection(edtLaiEnd)  // Di chuyển con trỏ về cuối văn bản
    }

    private fun clickHienMatKhau() {
        val edtEnd = binding.edtMatkhau.text.length
        if (binding.edtMatkhau.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            //ẨN MẬT KHẨU
            binding.edtMatkhau.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            // Đổi hình ảnh
            binding.btnHienMk.setImageResource(R.drawable.hienmk)
        } else {
            //HIỂN THỊ MẬT KHẨU
            binding.edtMatkhau.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            // Đổi hình ảnh
            binding.btnHienMk.setImageResource(R.drawable.anmk)
        }
        binding.edtMatkhau.setSelection(edtEnd)
    }


    // dẩy dữ liệu lên firebase
    private fun addTaiKhoanLenFỉebase() {
        // Tạo một id
/*        val id = myRef.push().key!!*/
        // LẤY GIÁ TRỊ EDT
        val img:String=""
        val taiKhoan = binding.edtTaikhoan.text.toString()
        val name = binding.edtName.text.toString()
        val matKhau = binding.edtMatkhau.text.toString()
        val laiMatKhau = binding.edtLaiMatKhau.text.toString()
        // KIỂM TRA CÁC EDT CÓ TRỐNG KHÔNG
        if (name.isEmpty()) {
            binding.edtName.error = "Nhập Tên bạn vào"
            return
        }
        if (taiKhoan.isEmpty()) {
            binding.edtTaikhoan.error = "Nhập tài khoản vào"
            return
        }
        if (matKhau.isEmpty()) {
            binding.edtMatkhau.error = "Nhập mật khẩu vào"
            return
        }
        if (laiMatKhau.isEmpty()) {
            binding.edtLaiMatKhau.error = "Nhập lại mật khẩu"
            return
        }
        if (laiMatKhau != matKhau) {
            binding.edtLaiMatKhau.error = "Mật khẩu không giống nhau"
            return
        }
        //ĐĂNG KÍ 1 TÀI KHOẢN BẰNG createUserWithEmailAndPassword
        fbAuth.createUserWithEmailAndPassword(taiKhoan,matKhau)
            .addOnCompleteListener(this) { Task ->
                if (Task.isSuccessful) {
                    progressDialog.show()
                    Log.d("LOGIN", "Đăng kí thành công")
                    // LẤY THÔNG TIN NGƯỜI DÙNG ĐÃ ĐĂNG KÝ THÀNH CÔNG
                    val idAth=Task.result.user
                    //LẤY ID
                    val id=idAth?.uid
                    if (id!=null)
                    {
                        // TẠO ĐỐI TƯỢNG TAIKHOAN CHỨA THÔNG TIN CẦN ĐƯA LÊN REALTIME DATABASE
                        val upUser = User(id,name,img,taiKhoan, matKhau)
                        // SỬ DỤNG DATABASEREFERENCE ĐỂ ĐƯA DỮ LIỆU LÊN FIREBASE REALTIME DATABASE
                        dbRef.child(id).setValue(upUser).addOnCompleteListener(this) { Task ->
                            if (Task.isSuccessful) {
                                //THÀNH CÔNG CHUYỂN MÀN HÌNH
                                loadDataUser()
                            }

                        }
                    }
                    else
                        Log.d("SSSSSSSSS","id is null")
                } else
                    Log.d("QQQQQWWWWW", "ĐĂNG KÍ THẤT BẠI ${Task.exception}")  //pass 6 ki tu tro len

            }
    }
    private fun loadDataUser() {
        viewModelUser.loadUser()
        viewModelUser.ldtGetUser().observe(this, Observer {
            userSqlHelper.deleteUser()
            it.forEach {user->
                if (user.id==user.id){
                    if (!user!!.id.equals(fbAuth.currentUser?.uid))
                    {
                        userSqlHelper.addUser(user)
                    }
                }
            }
            val intent = Intent(this, UserActivity::class.java)
            progressDialog.dismiss()
            startActivity(intent)
            finish()
        })

    }
}