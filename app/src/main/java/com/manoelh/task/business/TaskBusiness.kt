package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.repository.PriorityRepository
import com.manoelh.task.repository.TaskRepository

class TaskBusiness (context: Context){

    private val mPriorityRepository = PriorityRepository.getInstance(context)
    private val mTaskRepository = TaskRepository.getInstance(context)

    fun loadPriorities() :MutableList<PriorityEntity> = mPriorityRepository.listPriorities()

    fun loadTasks() :MutableList<TaskEntity> = mTaskRepository.listPriorities()

}