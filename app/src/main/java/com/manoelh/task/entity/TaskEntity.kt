package com.manoelh.task.entity

import java.util.*

data class TaskEntity (var id: String = "", var userId: String, var priorityId: String, var description: String, var completed: Boolean, var dueDate: Date)