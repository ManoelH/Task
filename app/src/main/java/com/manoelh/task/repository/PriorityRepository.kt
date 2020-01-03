package com.manoelh.task.repository

import android.content.Context
import android.database.Cursor
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY.COLUMNS.ID
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY.COLUMNS.DESCRIPTION
import com.manoelh.task.constants.DatabaseConstants.TABLES.PRIORITY
import com.manoelh.task.entity.PriorityEntity


class PriorityRepository (context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    companion object{
        private var INSTANCE: PriorityRepository? = null

        fun getInstance(context: Context): PriorityRepository {
            if(INSTANCE == null)
                INSTANCE = PriorityRepository(context)

            return INSTANCE as PriorityRepository
        }
    }

    fun loadPriorities() :MutableList<PriorityEntity>{
        var priorities = mutableListOf<PriorityEntity>()
        var cursor: Cursor
        val db = databaseHelper.readableDatabase
        val columns = arrayOf(ID, DESCRIPTION)
        cursor = db.query(PRIORITY.NAME, columns, null, null, null, null, ID)
        if (cursor.count > 0){
            cursor.moveToFirst()
            var i = 0
            while ( i < cursor.count ){
                var id = cursor.getLong(cursor.getColumnIndex(ID))
                var description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                var priority = PriorityEntity(id, description)
                priorities.add(priority)
                cursor.moveToNext()
                i++
            }
        }
        return priorities
    }
}