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

        fun getCachePriorityDescription(id: String) = prioritiesHashMap[id].toString()

        fun getCachePriorities(): List<PriorityEntity>{
            val priorities: MutableList<PriorityEntity> = arrayListOf()

            if (prioritiesHashMap.isNotEmpty()){
                val ids = prioritiesHashMap.keys.toList()

                for (i in 0 until prioritiesHashMap.size) {
                    val priority = PriorityEntity(ids[i], prioritiesHashMap[ids[i]]!!)
                    priorities.add(priority)
                }
            }
            return priorities
        }
    }
}