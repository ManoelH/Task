package com.manoelh.task.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.manoelh.task.constants.DatabaseConstants

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        private val DATABASE_NAME = "task.db"
        private val DATABASE_VERSION = 1
    }
    private val createTableUsers = """CREATE TABLE ${DatabaseConstants.DATABASE.TABLE_NAME}(
        ${DatabaseConstants.DATABASE.COLUMNS.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${DatabaseConstants.DATABASE.COLUMNS.NAME} TEXT,
        ${DatabaseConstants.DATABASE.COLUMNS.EMAIL} TEXT,
        ${DatabaseConstants.DATABASE.COLUMNS.PASSWORD} TEXT);"""

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}