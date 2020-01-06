package com.manoelh.task.repository

import com.manoelh.task.entity.PriorityEntity

class PriorityCache private constructor(){

    companion object{
        private var prioritiesHashMap = hashMapOf<Long, String>()

        fun setCache(priorities: List<PriorityEntity>){
            for (item in priorities){
                prioritiesHashMap[item.id] = item.description
            }
        }

        fun getCache(id: Long) = prioritiesHashMap[id].toString()
    }
}