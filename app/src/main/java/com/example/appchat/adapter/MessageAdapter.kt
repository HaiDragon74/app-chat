package com.example.appchat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.dataclass.Message
import com.example.appchat.`interface`.ViewModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MessageAdapter(val context: Context,val listMessage: MutableList<Message>) :
    RecyclerView.Adapter<ViewHolder>() {
    private val TYPE_User = 100
    private val TYPE_Receiver = 101
    var onItemClick: ((Message) -> Unit)? = null
    inner class UserViewHolder(itemView: View) : ViewHolder(itemView) {
        val txtUser = itemView.findViewById<TextView>(R.id.txtUser)
        var item: Message? = null
        val imgUser = itemView.findViewById<ImageView>(R.id.imgUser)
        val imgOneInsertUser = itemView.findViewById<ImageView>(R.id.imgOneInsertUser)
        val imgTowInsertUser = itemView.findViewById<ImageView>(R.id.imgTowInsertUser)
        val img1Img2User = itemView.findViewById<CardView>(R.id.img1Img2User)
        val timeUser = itemView.findViewById<TextView>(R.id.timeUser)
        val imgCheck = itemView.findViewById<ImageView>(R.id.imgCheck)

        init {
            txtUser.setOnClickListener { item?.let { onItemClick?.invoke(it) } }
        }

        fun bind(item: Message,isLastItem: Boolean) {
            this.item = item
                txtUser.text = item.message
                item.time?.let {
                    timeUser.text=SimpeleTime(it)
                }
            if (isLastItem) {
                checkMessage(item,imgCheck)
                item.time?.let {
                    timeUser.text=SimpeleTime(it)
                }
            } else {
                // Ẩn dấu tích cho các tin khác
                imgCheck.visibility = View.GONE
            }
            if (item.imgUser.toString().isNotEmpty())
                Glide.with(itemView.context).load(item.imgUser).into(imgUser)

            if (item.imgUri1.toString().isNotEmpty() && item.imgUri2.toString().isNotEmpty()){
                Glide.with(itemView.context).load(item.imgUri1) .into(imgOneInsertUser)
                Glide.with(itemView.context).load(item.imgUri2).into(imgTowInsertUser)
                img1Img2User.visibility = View.VISIBLE
                imgOneInsertUser.visibility = View.VISIBLE
                imgTowInsertUser.visibility = View.VISIBLE
            } else if (item.imgUri1.toString() == "" && item.imgUri2.toString() == "") {
                img1Img2User.visibility = View.GONE
                imgOneInsertUser.visibility = View.GONE
                imgTowInsertUser.visibility = View.GONE
            }else if (item.imgUri1.toString().isNotEmpty() && item.imgUri2.toString() == "") {
                Glide.with(itemView.context).load(item.imgUri1).into(imgOneInsertUser)
                img1Img2User.visibility = View.VISIBLE
                imgOneInsertUser.visibility = View.VISIBLE
                imgTowInsertUser.visibility = View.GONE
            }
            if (item.message == "")
                txtUser.visibility = View.INVISIBLE
            else
                txtUser.visibility = View.VISIBLE



        }
    }

    inner class ReceiverViewHolder(itemView: View) : ViewHolder(itemView) {
         fun bind(item: Message) {
            val txtReceiver = itemView.findViewById<TextView>(R.id.txtReceiver)
            val imgReceiver = itemView.findViewById<ImageView>(R.id.imgReceiver)
            val imgOneInsertReceiver = itemView.findViewById<ImageView>(R.id.imgOneInsertReceiver)
            val img1Img2Receiver = itemView.findViewById<CardView>(R.id.img1Img2Receiver)
            val imgTowInsertReceiver = itemView.findViewById<ImageView>(R.id.imgTowInsertReceiver)
            val timeReceiver = itemView.findViewById<TextView>(R.id.timeReceiver)
            val imgCheck = itemView.findViewById<ImageView>(R.id.imgCheck)
            txtReceiver.text = item.message.toString()
            Log.d("ahahahaha",item.message.toString())
            item.time?.let {
                timeReceiver.text=SimpeleTime(it)
            }
            if (item.imgUser.toString().isNotEmpty())
                Glide.with(itemView.context).load(item.imgReceiver).into(imgReceiver)
            if (item.imgUri1.toString().isNotEmpty() && item.imgUri2.toString().isNotEmpty()){
                Glide.with(itemView.context).load(item.imgLink1) .into(imgOneInsertReceiver)
                Glide.with(itemView.context).load(item.imgLink2).into(imgTowInsertReceiver)
                img1Img2Receiver.visibility = View.VISIBLE
                imgOneInsertReceiver.visibility = View.VISIBLE
                imgTowInsertReceiver.visibility = View.VISIBLE
            } else if (item.imgUri1.toString() == "" && item.imgUri2.toString() == "") {
                img1Img2Receiver.visibility = View.GONE
                imgOneInsertReceiver.visibility = View.GONE
                imgTowInsertReceiver.visibility = View.GONE
            }else if (item.imgUri1.toString().isNotEmpty() && item.imgUri2.toString() == "") {
                Glide.with(itemView.context).load(item.imgLink1).into(imgOneInsertReceiver)
                img1Img2Receiver.visibility = View.VISIBLE
                imgOneInsertReceiver.visibility = View.VISIBLE
                imgTowInsertReceiver.visibility = View.GONE
            }
            if (item.message == "")
                txtReceiver.visibility = View.GONE
            else
                txtReceiver.visibility = View.VISIBLE


        }
    }

    override fun getItemViewType(position: Int): Int {
        val firebaseId: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val idUser = firebaseId.uid
        return if (listMessage[position].idUser == idUser)
            TYPE_User
        else
            TYPE_Receiver
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_User) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dong_message_user, parent, false)
            UserViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dong_message_receiver, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? UserViewHolder)?.bind(listMessage[position],position == listMessage.size - 1)
        (holder as? ReceiverViewHolder)?.bind(listMessage[position])
    }
    private fun SimpeleTime(time:Long):String{
        val calendar=Calendar.getInstance()
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val month=calendar.get(Calendar.MONTH)
        val year=calendar.get(Calendar.YEAR)
        //LẤY THỨ
        val date=Date(time)
        calendar.time=date
        val dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)
        val currentTime="$day/0${month + 1}/$year"
        Log.d("dsasaass",currentTime.toString())
        var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedTime = simpleDateFormat.format(Date(time))
        Log.d("dsasaass", formattedTime)

        simpleDateFormat = if (currentTime == formattedTime) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("EEEE dd/MM/yyyy", Locale.getDefault())
        }
        return simpleDateFormat.format(Date(time))
    }
    private fun checkMessage(message: Message,imgSend:ImageView){
        var status:String=""
        val conversationId = if (message.idUser!! < message.idReceiver.toString()) {
            "${message.idUser}-${message.idReceiver}"
        } else {
            "${message.idReceiver}-${message.idUser}"
        }
        val dbref=FirebaseDatabase.getInstance().getReference("tinnhan")
            dbref.child(conversationId)
                .limitToLast(1)
                .addChildEventListener(object :ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val data=snapshot.child("trangthai").getValue(String::class.java)
                        data?.let { status=data }
                        if (status=="dagui"){
                            imgSend.visibility=View.VISIBLE
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        var data=snapshot.child("trangthai").getValue(String::class.java)
                        data=""
                        status=data

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

     }
}