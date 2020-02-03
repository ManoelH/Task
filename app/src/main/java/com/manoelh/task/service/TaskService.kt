package com.manoelh.task.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.util.ValidationException

class TaskService (val context: Context){

    private val TAG = "TaskService"
    private val db = FirebaseFirestore.getInstance()
    private val mTaskBusiness = TaskBusiness (context)

     fun insertTask(task: TaskEntity): MutableLiveData<String>{

         val idTask: MutableLiveData<String> = MutableLiveData()
        try {
            mTaskBusiness.validateTask(task)
            val taskDatabase =
                hashMapOf(
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.AUTHENTICATION_ID to task.userId,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED to task.completed,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION to task.description,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE to task.dueDate,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID to task.priorityId)
            db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME)
                .add(taskDatabase)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, context.getString(R.string.task_added_log) + documentReference.id)
                    Toast.makeText(context, context.getString(R.string.task_saved_message), Toast.LENGTH_LONG).show()
                    idTask.postValue(documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, context.getString(R.string.error_adding_task), e)
                    idTask.postValue("")
                }
        }catch (ve: ValidationException){
            Toast.makeText(context, ve.message, Toast.LENGTH_LONG).show()
            idTask.postValue("")
        }
        return idTask
    }

     fun loadTaskDataToUpdateFromActivity(taskId: String, userId: String): MutableLiveData<TaskEntity>{

        val taskLiveData: MutableLiveData<TaskEntity> = MutableLiveData()
        var taskEntity: TaskEntity

         db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(taskId).get()
             .addOnSuccessListener { document ->
                 Log.d(TAG, "${document.id} => ${document.data}")

                 taskEntity = TaskEntity(
                     document.id,
                     userId,
                     document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID).toString(),
                     document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION).toString(),
                     document.getBoolean(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED)!!,
                     document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE).toString()
                 )
                 taskLiveData.postValue(taskEntity)
             }
             .addOnFailureListener { exception ->
                 Log.w(TAG, context.getString(R.string.error_getting_task_to_update), exception)
                 taskLiveData.postValue(null)
             }

        return taskLiveData
    }

}