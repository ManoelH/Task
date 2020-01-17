package com.manoelh.task.constants

class DatabaseConstants {

    object COLLECTIONS{
        object USERS{
            val COLLECTION_NAME = "users"
            object ATTRIBUTES{
            val NAME = "name"
            val AUTHENTICATION_ID = "authentication_id"
            }
        }

        object TASKS{
            val COLLECTION_NAME = "tasks"
            object ATTRIBUTES{
                val AUTHENTICATION_ID = "authentication_id"
                val COMPLETED = "completed"
                val DESCRIPTION = "description"
                val DUE_DATE = "due_date"
                val PRIORITY_ID = "priority_id"
            }
        }

        object PRIORITIES{
            val COLLECTION_NAME = "priorities"
            object ATTRIBUTES{
                val DESCRIPTION = "description"
            }
        }
    }
}