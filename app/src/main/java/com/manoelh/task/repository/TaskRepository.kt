package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.USER_ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.PRIORITY_ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.DESCRIPTION
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.COMPLETED
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.DUE_DATE
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.util.SecurityPreferences

class TaskRepository  private constructor(context: Context) {

    private val mDatabaseHelper = DatabaseHelper(context)
    private val db = FirebaseFirestore.getInstance()

    companion object{
        private var INSTANCE: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository {
            if(INSTANCE == null)
                INSTANCE = TaskRepository(context)

            return INSTANCE as TaskRepository
        }
    }

    fun getTask(id: Long): TaskEntity?{
        var task: TaskEntity? = null
        try {
            val cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(ID, USER_ID, PRIORITY_ID, DESCRIPTION, COMPLETED, DUE_DATE)
            val selection = "$ID = ?"
            val selectionArgs = arrayOf(id.toString())
            cursor = db.query(TASK.NAME, columns, selection, selectionArgs, null, null, DUE_DATE)
            if (cursor.count > 0){
                cursor.moveToFirst()
               // task = getTaskOfCursor(cursor)
            }
            cursor.close()
        }catch (e: java.lang.Exception){
            throw e
        }
        return task
    }

    fun update(task: TaskEntity){
        try {
            val db = mDatabaseHelper.writableDatabase
            val updateValues = ContentValues()
            updateValues.put(USER_ID, task.userId)
            updateValues.put(PRIORITY_ID, task.priorityId)
            updateValues.put(DESCRIPTION, task.description)
            //updateValues.put(DUE_DATE, task.dueDate)
            updateValues.put(COMPLETED, task.completed)

            val whereClause = "$ID = ?"
            val whereArgs = arrayOf(task.id.toString())
            db.update(TASK.NAME, updateValues, whereClause, whereArgs)
        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }

    fun delete(taskId: Long){
        try {
            val db = mDatabaseHelper.writableDatabase
            val whereClause = "$ID = ?"
            val whereArgs = arrayOf(taskId.toString())
            db.delete(TASK.NAME, whereClause, whereArgs)
        }catch (e: Exception){
            throw e
        }
    }
}