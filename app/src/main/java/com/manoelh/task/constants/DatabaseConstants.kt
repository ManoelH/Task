package com.manoelh.task.constants

class DatabaseConstants {

    object TABLES{
        object USER {
            val NAME = "user"
            object COLUMNS {
                val ID = "id"
                val NAME = "name"
                val EMAIL = "email"
                val PASSWORD = "password"
            }
            object FIREBASE_COLUMNS{
                val NAME = "name"
                val AUTHENTICATION_ID = "authentication_id"
            }
        }

        object PRIORITY {
            val NAME = "priority"
            object COLUMNS{
                val ID = "id"
                val DESCRIPTION = "description"
            }
        }

        object TASK {
            val NAME = "task"
            object COLUMNS{
                val ID = "id"
                val USER_ID = "user_id"
                val PRIORITY_ID = "priority_id"
                val DESCRIPTION = "description"
                val DUE_DATE = "due_date"
                val COMPLETED = "completed"
            }
        }
    }
}