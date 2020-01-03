package com.manoelh.task.repository

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

    fun listPriorities() :MutableList<TaskEntity>{
        var tasksList = mutableListOf<TaskEntity>()
        try {
            var cursor: Cursor
            val db = mDatabaseHelper.readableDatabase
            val columns = arrayOf(ID, USER_ID, PRIORITY_ID, DESCRIPTION, COMPLETED, DUE_DATE)
            cursor = db.query(TASK.NAME, columns, null, null, null, null, ID)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                var i = 0
                while (i < cursor.count) {
                    var id = cursor.getLong(cursor.getColumnIndex(ID))
                    var userID = cursor.getLong(cursor.getColumnIndex(USER_ID))
                    var priorityID = cursor.getLong(cursor.getColumnIndex(PRIORITY_ID))
                    var description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                    var completed = cursor.getInt(cursor.getColumnIndex(COMPLETED))
                    var dueDate = cursor.getString(cursor.getColumnIndex(DUE_DATE))
                    var task = TaskEntity(id, userID, priorityID, description, completed, dueDate)
                    tasksList.add(task)
                    cursor.moveToNext()
                    i++
                }
            }
        }catch (e: Exception){
            return tasksList
        }
        return tasksList
    }
}