package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY.COLUMNS.ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY.COLUMNS.DESCRIPTION
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY
import com.manoelh.task.entity.PriorityEntity


class PriorityRepository private constructor(context: Context){

    private val mDatabaseHelper = DatabaseHelper(context)
    private val db = FirebaseFirestore.getInstance()

    companion object{
        private var INSTANCE: PriorityRepository? = null

        fun getInstance(context: Context): PriorityRepository {
            if(INSTANCE == null)
                INSTANCE = PriorityRepository(context)

            return INSTANCE as PriorityRepository
        }
    }

    fun listPriorities() :MutableList<PriorityEntity>{
        var priorities = mutableListOf<PriorityEntity>()

        try {
            db.collection("priorities")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val priorityEntity = PriorityEntity(document.id, document.get("description").toString())
                        priorities.add(priorityEntity)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
            /*val cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(ID, DESCRIPTION)
            cursor = db.query(PRIORITY.NAME, columns, null, null, null, null, ID)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                var i = 0
                while (i < cursor.count) {
                    var id = cursor.getInt(cursor.getColumnIndex(ID))
                    var description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                    var priority = PriorityEntity(id, description)
                    priorities.add(priority)
                    cursor.moveToNext()
                    i++
                }
            }*/
        }catch (e: Exception){
            return priorities
        }
        return priorities
    }
}