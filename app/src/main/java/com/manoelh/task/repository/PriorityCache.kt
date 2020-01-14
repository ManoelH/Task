package com.manoelh.task.repository

import com.manoelh.task.entity.PriorityEntity

class PriorityCache private constructor(){

    companion object{
        private var prioritiesHashMap = hashMapOf<String, String>()

        fun setCache(priorities: List<PriorityEntity>){
            for (item in priorities){
                prioritiesHashMap[item.id] = item.description
            }
        }

        fun getCache(id: String) = prioritiesHashMap[id].toString()
    }
}