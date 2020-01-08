package com.manoelh.task.interfaces

import com.manoelh.task.entity.TaskEntity

interface OnTaskListFragmentInteractionListener {

    fun onListClick(taskId: Long){}

    fun onDeleteClick(task: TaskEntity){}
}