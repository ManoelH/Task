package com.manoelh.task.service

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.repository.PriorityCache
import java.lang.Exception

class PriorityService(val context: Context) {

    private val TAG = "FirebaseFirestoreService"
    private val db = FirebaseFirestore.getInstance()

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