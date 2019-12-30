package com.manoelh.task.util

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences (context: Context){

    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences("task", Context.MODE_PRIVATE)

    fun storeString (key: String, value: String){
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun getStoreString(key: String) = mSharedPreferences.getString(key, "")

    fun removeStoreString(key: String){
        mSharedPreferences.edit().remove(key).apply()
    }
}