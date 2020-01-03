package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.repository.PriorityRepository

class TaskBusiness (context: Context){

    private val priorityRepository = PriorityRepository.getInstance(context)

    fun loadPriorities() :MutableList<PriorityEntity>{
        return priorityRepository.loadPriorities()
    }
}