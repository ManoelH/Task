package com.manoelh.task.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.repository.PriorityCache
import com.manoelh.task.util.SecurityPreferences
import java.lang.Exception

class FirebaseFirestoreService (val context: Context){

    private val TAG = "FirebaseFirestoreService"
    private val mSecurityPreferences = SecurityPreferences(context)
    private val db = FirebaseFirestore.getInstance()

    fun getUserName(): MutableLiveData<String>{
        val userName: MutableLiveData<String> = MutableLiveData()

        try {
            db.collection(DatabaseConstants.COLLECTIONS.USERS.COLLECTION_NAME)
                .whereEqualTo(
                    DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.AUTHENTICATION_ID,
                    mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val name = document.get(DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.NAME).toString()
                        userName.postValue(name)
                        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, name)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, context.getString(R.string.error_getting_user_name), exception)
                }

        }catch (e: Exception){
            throw e
        }
        return userName
    }

    fun searchPriorities(){
        val priorities = mutableListOf<PriorityEntity>()
        try {
            db.collection(DatabaseConstants.COLLECTIONS.PRIORITIES.COLLECTION_NAME)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val priorityEntity = PriorityEntity(document.id,
                            document.get(DatabaseConstants.COLLECTIONS.PRIORITIES.ATTRIBUTES.DESCRIPTION).toString())
                        priorities.add(priorityEntity)
                    }
                    PriorityCache.setCache(priorities)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, context.getString(R.string.error_getting_priorities), exception)
                }
        }catch (e: Exception){
            throw e
        }
    }

}