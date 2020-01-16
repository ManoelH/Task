package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.DatabaseConstants.TABLES.USER.COLUMNS.EMAIL
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception

class UserRepository private constructor(context: Context){

    private val mDatabaseHelper: DatabaseHelper = DatabaseHelper(context)
    private val TABLE_NAME = DatabaseConstants.TABLES.USER.NAME
    private val db = FirebaseFirestore.getInstance()
    private val mSecurityPreferences = SecurityPreferences(context)

    companion object{
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            if(INSTANCE == null)
                INSTANCE = UserRepository(context)

             return INSTANCE as UserRepository
        }
    }

    fun insert(name: String, id: String){
        val userDatabase =
            hashMapOf(DatabaseConstants.TABLES.USER.FIREBASE_COLUMNS.NAME to name,
                      DatabaseConstants.TABLES.USER.FIREBASE_COLUMNS.AUTHENTICATION_ID to id )
        db.collection("users")
            .add(userDatabase)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }

        /*val db = mDatabaseHelper.writableDatabase
        val insertValues = ContentValues()
        insertValues.put(NAME, user.name)
        insertValues.put(EMAIL, user.email)
        insertValues.put(PASSWORD, user.password)
        return db.insert(TABLE_NAME, null, insertValues)*/
    }

    fun thisEmailExist(userEmail: String): Boolean{
        val emailExist: Boolean
        try {
            val cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(EMAIL)
            val selection = "$EMAIL = ?"
            val selectionArgs = arrayOf(userEmail)
            cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)
            emailExist = cursor.count > 0
            cursor.close()
        }catch (e: Exception){
            throw e
        }
        return emailExist
    }
}