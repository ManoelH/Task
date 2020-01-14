package com.manoelh.task.entity

data class PriorityEntity (var id: String, var description: String){

    override fun toString(): String {
        return description
    }
}