package com.example.appchat.cosodulieusql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appchat.dataclass.User

class UserSqlHelper(context: Context) : SQLiteOpenHelper(context, "DATABASE_USER", null, 1) {
    companion object {
        const val TABLE_USER = "User"
        const val ID = "_id"
        const val ID_USER = "id"
        const val NAME = "name"
        const val IMG = "img"
        const val TAIKHOAN = "taiKhoan"
        const val MATKHAU = "matKhau"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_USER ($ID INTEGER PRIMARY KEY AUTOINCREMENT,$ID_USER TEXT,$NAME TEXT,$IMG TEXT,$TAIKHOAN TEXT,$MATKHAU TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun addUser(user: User):Long{
        val db =this.writableDatabase
        val value=ContentValues()
        value.put(ID_USER,user.id)
        value.put(NAME,user.name)
        value.put(IMG,user.img)
        value.put(TAIKHOAN,user.taiKhoan)
        value.put(MATKHAU,user.matKhau)
        val long=db.insert(TABLE_USER,null,value)
        db.close()
        return long

    }
    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_USER",null)
        cursor?.let {cursos->
            if (cursos.moveToFirst()) {
                do {
                    val user = User(
                        id = cursos.getString(cursos.getColumnIndex("id")),
                        name = cursos.getString(cursos.getColumnIndex("name")),
                        img = cursos.getString(cursos.getColumnIndex("img")),
                        taiKhoan = cursos.getString(cursos.getColumnIndex("taiKhoan")),
                        matKhau = cursos.getString(cursos.getColumnIndex("matKhau"))
                    )
                    userList.add(user)
                } while (cursos.moveToNext())
            }
        }
        return userList
    }
    fun deleteUser():Int{
        val db=this.writableDatabase
        val result=db.delete(TABLE_USER,null,null)
        db.close()
        return result
    }
    fun deleteUserById(user: User):Int{
        val db=this.writableDatabase
        val result=db.delete(TABLE_USER,"$ID_USER=?", arrayOf(user.id))
        db.close()
        return result
    }
    fun updateUserById(user: User):Int{
        val db =this.writableDatabase
        val value=ContentValues()
        value.put(ID_USER,user.id)
        value.put(NAME,user.name)
        value.put(IMG,user.img)
        value.put(TAIKHOAN,user.taiKhoan)
        value.put(MATKHAU,user.matKhau)
        val result=db.update(TABLE_USER,value,"$ID_USER=?", arrayOf(user.id))
        db.close()
        return result
    }
    fun stanking(user: User){
        
    }


}