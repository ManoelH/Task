package com.manoelh.task.entity

data class TaskEntity (var id: Long = 0, var userId: Long, var priorityId: String, var description: String, var completed: Int, var dueDate: String)