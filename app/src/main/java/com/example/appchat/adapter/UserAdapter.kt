package com.example.appchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.dataclass.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserAdapter(private val context: Context, private var listUser:MutableList<User>):RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    lateinit var click:((User) -> Unit)
    class ViewHolder(imageView:View):RecyclerView.ViewHolder(imageView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.dong_user,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            val imgUser=findViewById<ImageView>(R.id.imgUser)
            val txtUser=findViewById<TextView>(R.id.txtUser)
            Glide.with(context).load(listUser[position].img).placeholder(R.drawable.avt).into(imgUser)
            txtUser.text=listUser[position].name


            //click item rcl
            holder.itemView.setOnClickListener {
                click.invoke(listUser[position])
            }

        }
    }
}