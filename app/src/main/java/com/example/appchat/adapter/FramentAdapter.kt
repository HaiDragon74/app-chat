package com.example.appchat.adapter

import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appchat.R
import com.example.appchat.cosodulieusql.MessageLastSqlHelper
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.dataclass.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FramentAdapter():RecyclerView.Adapter<FramentAdapter.ViewHolder>() {
    lateinit var onClick:((Message)->Unit)

    private var listMessage:MutableList<Message> = mutableListOf()
    private lateinit var context:android.content.Context

    fun getMessage(context: android.content.Context,listMessage:MutableList<Message> ){
        this.context=context
        this.listMessage=listMessage
        notifyDataSetChanged()
    }
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val imgReceiver=itemView.findViewById<ImageView>(R.id.imgAvtReceiver)
        val nameReceiver=itemView.findViewById<TextView>(R.id.txtNameReceiver)
        val message=itemView.findViewById<TextView>(R.id.txtMessage)
        val txtTimeUserReacevier=itemView.findViewById<TextView>(R.id.txtTimeUserReacevier)
        var item:Message?=null

        init {
            itemView.setOnClickListener { item?.let {message->
                onClick.invoke(message)
            } }
        }

        fun bind(item:Message){
            val sqlHelper=MessageLastSqlHelper(context)
            val listMessageLast=sqlHelper.readListMessageLastSql()
            Log.d("HSHSAHAHAHAH",listMessageLast.toString())
            this.item=item
            if (item.imgUser.toString().isNotEmpty())
                Glide.with(itemView.context).load(item.imgReceiver).into(imgReceiver)
            nameReceiver.text=item.nameReceiver
            message.text=item.message
            item.time?.let { txtTimeUserReacevier.text=SimpeleTime(it)}
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.dong_rclframent,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMessage[position])
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
}