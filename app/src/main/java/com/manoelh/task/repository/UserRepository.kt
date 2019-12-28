package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.DatabaseConstants.DATABASE.COLUMNS.PASSWORD
import com.manoelh.task.constants.DatabaseConstants.DATABASE.COLUMNS.EMAIL
import com.manoelh.task.constants.DatabaseConstants.DATABASE.COLUMNS.NAME
import com.manoelh.task.entity.UserEntity


class UserRepository private constructor(context: Context){

    private val mDatabaseHelper: DatabaseHelper = DatabaseHelper(context)

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
        return db.insert(DatabaseConstants.DATABASE.TABLE_NAME, null, contentValues)
    }
}