package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.repository.PriorityRepository
import com.manoelh.task.repository.TaskRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException

class TaskBusiness (var context: Context){

    private val mTaskRepository = TaskRepository.getInstance(context)
    private var mSecurityPreferences = SecurityPreferences(context)


    fun loadTasks(taskFilterCompleted: Int) :MutableList<TaskEntity> {
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)
        return mTaskRepository.listTasks(userId!!.toLong(), taskFilterCompleted)
    }
    fun insertTask(task: TaskEntity): Long{
        validateTask(task)
        return mTaskRepository.insert(task)
    }

    private fun validateTask(task: TaskEntity){
        if (task.description.isBlank() || task.dueDate.isBlank())
            throw ValidationException(context.getString(R.string.valuesEmpty))
        else if (task.completed < 0 || task.completed > 1)
            throw ValidationException(context.getString(R.string.checkBoxError))
    }

    fun loadTaskById(taskId: Long) = mTaskRepository.getTask(taskId)

    fun updateTask(task: TaskEntity){
        mTaskRepository.update(task)
    }

    fun deleteTask(taskId: Long){
        mTaskRepository.delete(taskId)
    }
}