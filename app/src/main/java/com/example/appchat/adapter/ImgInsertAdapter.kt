package com.example.appchat.adapter

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appchat.databinding.DongRclimginsertBinding
import com.google.firebase.database.core.Context

class ImgInsertAdapter(private var context: android.content.Context,private var contentResolver: ContentResolver):RecyclerView.Adapter<ImgInsertAdapter.ViewHolder>() {

    private var list:MutableList<Uri> = mutableListOf()
    private lateinit var myContext:android.content.Context

    fun getAdapter(myContext: android.content.Context,mutableList: MutableList<Uri>){
        this.list=mutableList
        this.myContext=myContext
        notifyDataSetChanged()
    }
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolder(binding: DongRclimginsertBinding):RecyclerView.ViewHolder(binding.root) {
        val imgstore=binding.imgInsert
        val txtOff=binding.txtOff
        fun bind(contentResolver: ContentResolver,uri: Uri):Bitmap?{
            return try {
                val inputStream=contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
            catch (err:Exception){
                Toast.makeText(context,err.message.toString(),Toast.LENGTH_SHORT).show()
                null
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=DongRclimginsertBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri=list[position]
        val binMap= holder.bind(contentResolver,uri)
        binMap.let {
            holder.imgstore.setImageBitmap(it)
        }
        holder.txtOff.setOnClickListener {
            val count = list.size - position
                list.removeAt(position)
                // Notify adapter about the item removal
                notifyItemRangeRemoved(position, count)

        }
    }
}