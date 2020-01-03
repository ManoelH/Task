package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.repository.PriorityRepository
import com.manoelh.task.repository.TaskRepository
import com.manoelh.task.util.ValidationException

class TaskBusiness (context: Context){

    private val mPriorityRepository = PriorityRepository.getInstance(context)
    private val mTaskRepository = TaskRepository.getInstance(context)

    fun loadPriorities() :MutableList<PriorityEntity> = mPriorityRepository.listPriorities()

    fun loadTasks(userId: Long) :MutableList<TaskEntity> = mTaskRepository.listTasks(userId)

    fun insertTask(task: TaskEntity): Long{
        validateTask(task)
        return mTaskRepository.insert(task)
    }

    fun validateTask(task: TaskEntity){
        if (task.description.isBlank() || task.dueDate.isBlank())
            throw ValidationException("Error there are some value(s) empty")
        else if (task.completed < 0 || task.completed > 1)
            throw ValidationException("Error value of completed is not valid")
    }
}