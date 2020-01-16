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

    fun listTasks(userId: String, taskFilterCompleted: Boolean) :MutableList<TaskEntity>{
        val tasksList = mutableListOf<TaskEntity>()
        try {

            db.collection("tasks").whereEqualTo("authentication_id", userId)
                .whereEqualTo("completed", taskFilterCompleted).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val taskEntity = TaskEntity(
                            document.id,
                            userId,
                            document.get("priority_id").toString(),
                            document.get("description").toString(),
                            document.getBoolean("completed")!!,
                            document.getDate("due_date")!!
                        )
                        tasksList.add(taskEntity)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }

            /*val cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(ID, USER_ID, PRIORITY_ID, DESCRIPTION, COMPLETED, DUE_DATE)
            val selection = "$USER_ID = ? AND $COMPLETED = ?"
            val selectionArgs = arrayOf(userId.toString(), taskFilterCompleted.toString())
            cursor = db.query(TASK.NAME, columns, selection, selectionArgs, null, null, DUE_DATE)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                var i = 0
                while (i < cursor.count) {
                    tasksList.add(getTaskOfCursor(cursor))
                    cursor.moveToNext()
                    i++
                }
            }
            cursor.close()*/
        }catch (e: Exception){
            return tasksList
        }
        return tasksList
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

/*    private fun getTaskOfCursor(cursor: Cursor): TaskEntity{
        val taskId = cursor.getString(cursor.getColumnIndex(ID))
        val userID = cursor.getString(cursor.getColumnIndex(USER_ID))
        val priorityID = cursor.getString(cursor.getColumnIndex(PRIORITY_ID))
        val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
        val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
        val dueDate = cursor.getDa(cursor.getColumnIndex(DUE_DATE))
        return TaskEntity(taskId, userID, priorityID, description, completed, dueDate)
    }*/

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