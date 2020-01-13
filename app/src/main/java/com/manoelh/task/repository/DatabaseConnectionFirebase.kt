package com.manoelh.task.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.entity.UserEntity

class DatabaseConnectionFirebase {
    // Access a Cloud Firestore instance from your Activity
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(user: UserEntity){
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}