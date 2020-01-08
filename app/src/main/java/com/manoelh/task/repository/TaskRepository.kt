package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.USER_ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.PRIORITY_ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.DESCRIPTION
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.COMPLETED
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK.COLUMNS.DUE_DATE
import com.manoelh.task.constants.DatabaseConstants.TABLES.TASK

class TaskRepository  private constructor(context: Context) {

    private val mDatabaseHelper = DatabaseHelper(context)

    companion object{
        private var INSTANCE: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository {
            if(INSTANCE == null)
                INSTANCE = TaskRepository(context)

            return INSTANCE as TaskRepository
        }
    }

    fun listTasks(userId: Long, taskFilterCompleted: Int) :MutableList<TaskEntity>{
        val tasksList = mutableListOf<TaskEntity>()
        try {
            val cursor: Cursor
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
            cursor.close()
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
                task = getTaskOfCursor(cursor)
            }
            cursor.close()
        }catch (e: java.lang.Exception){
            throw e
        }
        return task
    }

    private fun getTaskOfCursor(cursor: Cursor): TaskEntity{
        val taskId = cursor.getLong(cursor.getColumnIndex(ID))
        val userID = cursor.getLong(cursor.getColumnIndex(USER_ID))
        val priorityID = cursor.getInt(cursor.getColumnIndex(PRIORITY_ID))
        val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
        val completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
        val dueDate = cursor.getString(cursor.getColumnIndex(DUE_DATE))
        return TaskEntity(taskId, userID, priorityID, description, completed, dueDate)
    }

    fun insert(task: TaskEntity): Long{
        val id: Long
        try {
            val db = mDatabaseHelper.writableDatabase
            val insertValues = ContentValues()
            insertValues.put(USER_ID, task.userId)
            insertValues.put(PRIORITY_ID, task.priorityId)
            insertValues.put(DESCRIPTION, task.description)
            insertValues.put(DUE_DATE, task.dueDate)
            insertValues.put(COMPLETED, task.completed)
            id = db.insert(TASK.NAME, null, insertValues)
        }catch (e: Exception){
            throw e
        }
        return id
    }

    fun update(task: TaskEntity){
        try {
            val db = mDatabaseHelper.writableDatabase
            val updateValues = ContentValues()
            updateValues.put(USER_ID, task.userId)
            updateValues.put(PRIORITY_ID, task.priorityId)
            updateValues.put(DESCRIPTION, task.description)
            updateValues.put(DUE_DATE, task.dueDate)
            updateValues.put(COMPLETED, task.completed)

            val whereClause = "$ID = ?"
            val whereArgs = arrayOf(task.id.toString())
            db.update(TASK.NAME, updateValues, whereClause, whereArgs)
        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }

    fun delete(task: TaskEntity){
        try {
            val db = mDatabaseHelper.writableDatabase
            val whereClause = "$ID = ?"
            val whereArgs = arrayOf(task.id.toString())
            db.delete(TASK.NAME, whereClause, whereArgs)
        }catch (e: Exception){
            throw e
        }
    }
}