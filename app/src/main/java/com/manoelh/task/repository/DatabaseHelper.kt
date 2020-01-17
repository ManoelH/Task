package com.manoelh.task.repository

/*OBS.:
    * @author : Manoel Henrique
    * THIS CLASS IS NOT USED, NO MORE, NOW THIS CLASS IT'S HERE TO SHOW MY KNOWLEDGE WITH SQLITE.
    * NOW THIS PROJECT IS A FIREBASE PROJECT WITH FIRESTORE DATABASE*/


/*
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.manoelh.task.constants.DatabaseConstants

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){


    companion object {
        private val DATABASE_NAME = "task.db"
        private val DATABASE_VERSION = 2
    }
    private val createTableUser = """CREATE TABLE ${DatabaseConstants.TABLES.USER.NAME}(
        ${DatabaseConstants.TABLES.USER.COLUMNS.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${DatabaseConstants.TABLES.USER.COLUMNS.NAME} TEXT,
        ${DatabaseConstants.TABLES.USER.COLUMNS.EMAIL} TEXT,
        ${DatabaseConstants.TABLES.USER.COLUMNS.PASSWORD} TEXT);"""

    private val createTablePriority = """CREATE TABLE ${DatabaseConstants.TABLES.PRIORITY.NAME}(
        ${DatabaseConstants.TABLES.PRIORITY.COLUMNS.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${DatabaseConstants.TABLES.PRIORITY.COLUMNS.DESCRIPTION} TEXT);"""

    private val createTableTask = """CREATE TABLE ${DatabaseConstants.TABLES.TASK.NAME}(
        ${DatabaseConstants.TABLES.TASK.COLUMNS.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${DatabaseConstants.TABLES.TASK.COLUMNS.USER_ID} INTEGER,
        ${DatabaseConstants.TABLES.TASK.COLUMNS.PRIORITY_ID} INTEGER,
        ${DatabaseConstants.TABLES.TASK.COLUMNS.DESCRIPTION} TEXT,
        ${DatabaseConstants.TABLES.TASK.COLUMNS.DUE_DATE} TEXT,
        ${DatabaseConstants.TABLES.TASK.COLUMNS.COMPLETED} INTEGER);"""

    private val insertPriorities = """INSERT INTO ${DatabaseConstants.TABLES.PRIORITY.NAME}
        values (1, 'low'), (2, 'normal'), (3, 'high'), (4, 'critic');"""

    private val dropTableUser = """DROP TABLE IF EXISTS ${DatabaseConstants.TABLES.USER.NAME};"""
    private val dropTablePriority = """DROP TABLE IF EXISTS ${DatabaseConstants.TABLES.PRIORITY.NAME};"""
    private val dropTableTask = """DROP TABLE IF EXISTS ${DatabaseConstants.TABLES.TASK.NAME};"""

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableUser)
        db.execSQL(createTablePriority)
        db.execSQL(createTableTask)
        db.execSQL(insertPriorities)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(dropTableUser)
        db.execSQL(dropTablePriority)
        db.execSQL(dropTableTask)
        onCreate(db)
    }
}*/
