package com.manoelh.task.entity

data class TaskEntity (var id: Long = 0, var user_id: Long, var priority_id: Long, var description: String, var completed: Int, var dueDate: String)