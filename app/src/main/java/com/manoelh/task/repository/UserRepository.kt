package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.DatabaseConstants.TABLES.USER.COLUMNS.PASSWORD
import com.manoelh.task.constants.DatabaseConstants.TABLES.USER.COLUMNS.EMAIL
import com.manoelh.task.constants.DatabaseConstants.TABLES.USER.COLUMNS.NAME
import com.manoelh.task.constants.DatabaseConstants.TABLES.USER.COLUMNS.ID
import com.manoelh.task.entity.UserEntity
import java.lang.Exception


class UserRepository private constructor(context: Context){

    private val mDatabaseHelper: DatabaseHelper = DatabaseHelper(context)
    private val TABLE_NAME = DatabaseConstants.TABLES.USER.NAME

    companion object{
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            if(INSTANCE == null)
                INSTANCE = UserRepository(context)

             return INSTANCE as UserRepository
        }
    }

    fun insert(user: UserEntity): Long{
        val db = mDatabaseHelper.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(NAME, user.name)
        contentValues.put(EMAIL, user.email)
        contentValues.put(PASSWORD, user.password)
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun thisEmailExist(userEmail: String): Boolean{
        var emailExist: Boolean
        try {
            var cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(EMAIL)
            val selectionArgs = arrayOf(userEmail)
            cursor = db.query(TABLE_NAME, columns, "$EMAIL = ?", selectionArgs, null, null, null)
            emailExist = cursor.count > 0
            cursor.close()
        }catch (e: Exception){
            throw e
        }
        return emailExist
    }

    fun login(userEmail: String, userPassword: String): UserEntity?{
        var user: UserEntity? = null
        try {
            var cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(ID, NAME, EMAIL)
            val selectionArgs = arrayOf(userEmail, userPassword)
            cursor = db.query(TABLE_NAME, columns, "$EMAIL = ? and $PASSWORD = ?", selectionArgs,
                null, null, null)
            if (cursor.count > 0){
                cursor.moveToFirst()
                val id = cursor.getLong(cursor.getColumnIndex(ID))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val email = cursor.getString(cursor.getColumnIndex(EMAIL))
                user = UserEntity(id, name, email)
            }
            cursor.close()
        }catch (e: Exception){
            throw e
        }
        return user
    }
}