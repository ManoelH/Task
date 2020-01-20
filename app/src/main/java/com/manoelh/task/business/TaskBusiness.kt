package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.R
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.util.ValidationException

class TaskBusiness (var context: Context){

    fun validateTask(task: TaskEntity){
        if (task.description.isBlank())
            throw ValidationException(context.getString(R.string.description_validation))
        if (task.dueDate.isBlank() || task.dueDate.equals("date", ignoreCase = true))
            throw ValidationException(context.getString(R.string.date_validation))
    }
}