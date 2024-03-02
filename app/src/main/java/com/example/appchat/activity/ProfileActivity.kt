package com.example.appchat.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.databinding.ActivityProfileBinding
import com.example.appchat.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var fbUser: FirebaseUser
    private lateinit var dbRef: DatabaseReference

    //LQ THƯ VIỆN DT VÀ THƯ VIỆN FIREBASE STORAGE
    private lateinit var fbStore: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var file: Uri? = null
    private val PICK = 2024
    private lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //LATEINIT
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // LẤY THÔNG TIN NGƯỜI DÙNG
        fbUser = FirebaseAuth.getInstance().currentUser!!
        // LẤY THAM CHIẾU ĐẾN
        dbRef =
            FirebaseDatabase.getInstance().getReference("taikhoan").child(fbUser.uid)
        Log.d("LLLLLLLLL", "${fbUser.uid}")


        //THƯ VIỆN DT IMG FIREBASE
        // KHỞI TẠO VÀ LẤY ĐỐI TƯỢNG
        fbStore = FirebaseStorage.getInstance()
        // LẤY THAM CHIẾU
        storageRef = fbStore.reference

        //CLICK IMGEXIT
        binding.imgExit.setOnClickListener {
            blackUserActivity()
        }
        //MO THU VIEN DTH
        binding.imgUser.setOnClickListener {
            addImage()
            binding.btnSave.visibility = View.VISIBLE
        }
        binding.btnSave.setOnClickListener {
            upLoadingImg()
        }
        //setimgprofiles
        setImgProfile()
    }

    private fun setImgProfile() {
        // Thêm một sự kiện lắng nghe cho thay đổi dữ liệu
        dbRef.addValueEventListener(object : ValueEventListener {
            // Phương thức được gọi khi dữ liệu trên "taikhoan" thay đổi
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lấy giá trị từ DataSnapshot và chuyển đổi thành đối tượng TaiKhoan
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.txtUser.setText(user.name.toString())
                    // Kiểm tra xem ảnh đại diện có tồn tại hay không
                } else {
                    Log.d("UserInfo", "taiKhoan is null")
                }
                if (user != null && user.img == "")
                    binding.imgUser.setImageResource(R.drawable.avt)
                else
                    Glide.with(this@ProfileActivity).load(user!!.img).into(binding.imgUser)
            }

            // Phương thức được gọi khi có lỗi
            override fun onCancelled(error: DatabaseError) {
                Log.d("QQQQQQ", "that bai")
            }
        })
    }

    //UPLOAD IMG LÊN FIREBASESTORE
    private fun upLoadingImg() {
        //LẤY GIÁ TRỊ EDT
        val editname = binding.txtUser.text.toString()
        //DIALOA LOADING
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        progressDialog.setTitle("Đang tải")
        if (file != null) {
            //TẠO THƯ MỤC LƯU IMG LÀ AVT,LƯU TÊN THEO UID
            val avtRef = storageRef.child("avatars/${FirebaseAuth.getInstance().currentUser!!.uid}")
            //ĐƯA IMG LÊN FIREBASE STORRAGE
            avtRef.putFile(file!!)
                .addOnSuccessListener {
                    //ĐƯA IMG LÊN THÀNH CÔNG LẤY LINK IMG
                    avtRef.downloadUrl.addOnSuccessListener { url ->
                        //LẤY LINK GÁN VÀO urlImg
                        val urlImg = url.toString()
                        Toast.makeText(this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show()
                        //ĐÓNG HỘP THOẠI DIALOG
                        progressDialog.dismiss()
                        // TẠO MỘT HASHMAP ĐỂ CHỨA DỮ LIỆU
                        val hashMath: HashMap<String, String> = HashMap()
                        hashMath.put("name", editname) // THÊM NAME VÀO HASHMAP
                        hashMath.put("img", urlImg) // THÊM ĐƯỜNG DẪN IMG
                        Log.d("QQQQQQQQQ", urlImg)
                        // CẬP NHẬT FIREBASE VỚI NAME VÀ IMG MỚI CÒN LẠI GIỮ NGUYÊN
                        dbRef.updateChildren(hashMath as Map<String, Any>)
                    }

                }
                //XỦ LÝ KHI LỖI XẢY RA
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        }
        /*private fun upLoadingImg() {
        if (file!=null)
                {
                    val progressDialog=ProgressDialog(this)
                    progressDialog.setTitle("Loading...")
                    progressDialog.show()
                    val avtRef=storageRef.child("avatars/${FirebaseAuth.getInstance().currentUser!!.uid}")
                    avtRef.putFile(file!!)
                        .addOnSuccessListener {
                            OnSuccessListener<UploadTask.TaskSnapshot>{
                                progressDialog.dismiss()
                                Toast.makeText(this,"UpLoading thành công",Toast.LENGTH_SHORT).show()
                            }

                        }
                        .addOnFailureListener {
                            OnFailureListener{
                                progressDialog.dismiss()
                                Toast.makeText(this,"UpLoading that bai",Toast.LENGTH_SHORT).show()
                            }

                        }
                        .addOnPausedListener {
                            OnPausedListener<UploadTask.TaskSnapshot>{
                                var double:Double=(100.0*it.bytesTransferred/it.totalByteCount)
                                progressDialog.setMessage("Upload"+ double.toInt()+"%")
                            }
                        }
                         }
                }*/
    }

    //CHỌN 1 ẢNH TỪ THƯ VIỆN ĐIỆN THOẠI
    private fun addImage() {
        val intent = Intent()
        // ĐẶT LOẠI DỮ LIỆU MÀ INTENT SẼ CHỌN.
        // Ở ĐÂY, ĐẶT LOẠI LÀ "IMAGE/*", CÓ NGHĨA
        // LÀ INTENT SẼ CHỌN BẤT KỲ LOẠI HÌNH ẢNH NÀO.
        intent.type = "image/*"
        // ĐẶT HÀNH ĐỘNG CỦA INTENT THÀNH ACTION_GET_CONTENT.
        // THƯỜNG ĐƯỢC SỬ DỤNG KHI MUỐN NGƯỜI DÙNG CHỌN NỘI DUNG TỪ HỆ THỐNG.
        intent.action = Intent.ACTION_GET_CONTENT
        // BẮT ĐẦU MỘT HOẠT ĐỘNG ĐỂ CHỌN NỘI DUNG VÀ
        // ĐỢI KẾT QUẢ BẰNG CÁCH SỬ DỤNG STARTACTIVITYFORRESULT
        startActivityForResult(intent, PICK)
    }

    //onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // KIỂM TRA XEM REQUESTCODE CÓ PHẢI LÀ MÃ YÊU CẦU ĐÃ ĐẶT TRƯỚC ĐÓ KHI GỌI STARTACTIVITYFORRESULT
        // VÀ RESULTCODE CÓ PHẢI LÀ RESULT_OK, TỨC LÀ NGƯỜI DÙNG ĐÃ CHỌN MỘT TẬP TIN THÀNH CÔNG.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK && resultCode == RESULT_OK) {
            // Lấy đối tượng URI của tập tin đã chọn từ Intent.
            file = data!!.data
            //DÙNG THƯ VIỆN GLIDE LOAD HÌNH ẢNH VÀO IMG
            Glide.with(this).load(file).into(binding.imgUser)
        }


    }

    //QUAY LAI MAN HINH USER ACTIVITY
    private fun blackUserActivity() {
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
    }
}






