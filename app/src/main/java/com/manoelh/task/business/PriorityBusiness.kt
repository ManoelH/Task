package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.repository.PriorityRepository

class PriorityBusiness (var context: Context) {
    private val mPriorityRepository = PriorityRepository.getInstance(context)

    fun loadPriorities() :MutableList<PriorityEntity> = mPriorityRepository.listPriorities()
}