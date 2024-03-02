package com.example.appchat.cosodulieusql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User

class MessageSqlHelper(context: Context):SQLiteOpenHelper(context,"DATABASE_MESSAGE",null,2) {

    companion object{
        const val TABLE_MESSAGE="TableMessage"
        const val ID_MESSAGE="_id"
        const val IDUSER="idUser"
        const val IDRECEIVER="idReceiver"
        const val NAMERECEIVER="nameReceiver"
        const val IMGUSER="imgUser"
        const val IMGRECEIVER="imgReceiver"
        const val IMGURI1="imgUri1"
        const val IMGURI2="imgUri2"
        const val IMGLINK1="imgLink1"
        const val IMGLINK2="imgLink2"
        const val MESSAGE="message"
        const val TIME="time"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_MESSAGE($ID_MESSAGE INTEGER PRIMARY KEY AUTOINCREMENT,$IDUSER TEXT,$IDRECEIVER TEXT,$NAMERECEIVER TEXT,$IMGUSER TEXT,$IMGRECEIVER TEXT,$IMGURI1 TEXT,$IMGURI2 TEXT,$IMGLINK1 TEXT,$IMGLINK2 TEXT,$MESSAGE TEXT,$TIME TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGE")
        onCreate(db)

    }
    fun addMessage(message: Message):Long{
        val db=this.writableDatabase
        val value=ContentValues()
        value.put(IDUSER,message.idUser)
        value.put(IDRECEIVER,message.idReceiver)
        value.put(NAMERECEIVER,message.nameReceiver)
        value.put(IMGUSER,message.imgUser)
        value.put(IMGRECEIVER,message.imgReceiver)
        value.put(IMGURI1,message.imgUri1)
        value.put(IMGURI2,message.imgUri2)
        value.put(IMGLINK1,message.imgLink1)
        value.put(IMGLINK2,message.imgLink2)
        value.put(MESSAGE,message.message)
        value.put(TIME,message.time)
        val long=db.insert(TABLE_MESSAGE,null,value)
        db.close()
        return long
    }
    fun readListMessageSql():List<Message>{
        val listMessage= mutableListOf<Message>()
        val db=this.readableDatabase
        val cs=db.rawQuery("SELECT * FROM $TABLE_MESSAGE",null)
        cs?.let {cursor ->
            if (cursor.moveToFirst()){
                do {
                    val message=Message(
                        idUser = cursor.getString(cursor.getColumnIndex("idUser")),
                        idReceiver = cursor.getString(cursor.getColumnIndex("idReceiver")),
                        nameReceiver =cursor.getString(cursor.getColumnIndex("nameReceiver")),
                        imgUser = cursor.getString(cursor.getColumnIndex("imgUser")),
                        imgReceiver =cursor.getString(cursor.getColumnIndex("imgReceiver")),
                        imgUri1 = cursor.getString(cursor.getColumnIndex("imgUri1")),
                        imgUri2 = cursor.getString(cursor.getColumnIndex("imgUri2")),
                        imgLink1 = cursor.getString(cursor.getColumnIndex("imgLink1")),
                        imgLink2 = cursor.getString(cursor.getColumnIndex("imgLink2")),
                        message = cursor.getString(cursor.getColumnIndex("message")),
                        time = cursor.getLong(cursor.getColumnIndex("time"))
                    )
                    listMessage.add(message)

                }while (cursor.moveToNext())
            }
        }
        return listMessage
    }
    fun updateUserByIdUser(message: Message):Int{
        val db =this.writableDatabase
        val value=ContentValues()
        value.put(IDUSER,message.idUser)
        value.put(IDRECEIVER,message.idReceiver)
        value.put(NAMERECEIVER,message.nameReceiver)
        value.put(IMGUSER,message.imgUser)
        value.put(IMGRECEIVER,message.imgReceiver)
        value.put(IMGURI1,message.imgUri1)
        value.put(IMGURI2,message.imgUri2)
        value.put(IMGLINK1,message.imgLink1)
        value.put(IMGLINK2,message.imgLink2)
        value.put(MESSAGE,message.message)
        val updater=db.update(TABLE_MESSAGE,value,"$IDUSER=?", arrayOf(message.idUser))
        db.close()
        return updater
    }
    fun deleteMessageByTime(message: Message):Int{
        val db=this.writableDatabase
        val result=db.delete(TABLE_MESSAGE,"$TIME=?", arrayOf(message.time.toString()))
        db.close()
        return result
    }


}