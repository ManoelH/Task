package com.manoelh.task.interfaces

import com.manoelh.task.entity.TaskEntity

interface OnTaskListFragmentInteractionListener {

    fun onListClick(taskId: String)

    fun onDeleteClick(task: TaskEntity)

    fun onImageCompletedClick(task: TaskEntity)
}