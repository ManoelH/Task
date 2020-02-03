package com.manoelh.task.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException

private const val TAG = "TaskRepository"

class TaskRepository (val context: Context){

    private val mSecurityPreferences = SecurityPreferences(context)
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

    fun updateTask(task: TaskEntity): MutableLiveData<String>{

        val idTask: MutableLiveData<String> = MutableLiveData()
        try {
            mTaskBusiness.validateTask(task)

            db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(task.id)
                .update(mapOf(
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED to task.completed,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION to task.description,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE to task.dueDate,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID to task.priorityId
                ))
                .addOnSuccessListener {
                    Log.d(TAG, context.getString(R.string.task_updated_log) +task.id)
                    Toast.makeText(context, context.getString(R.string.task_updated_message), Toast.LENGTH_LONG).show()
                    idTask.postValue(task.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, context.getString(R.string.error_update_task), e)
                    idTask.postValue(null)
                }
        }catch (ve: ValidationException){
            Toast.makeText(context, ve.message, Toast.LENGTH_LONG).show()
            idTask.postValue(null)
        }
        return idTask
    }

    fun deleteTask(id: String): MutableLiveData<String>{

        val idTaskDeleted: MutableLiveData<String> = MutableLiveData()

        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, context.getString(R.string.task_deleted_log))
                Toast.makeText(context, context.getText(R.string.task_deleted_message), Toast.LENGTH_LONG).show()
                idTaskDeleted.postValue(id)
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, context.getString(R.string.error_deleting_task), e)
                Toast.makeText(context, context.getText(R.string.error_deleting_task), Toast.LENGTH_LONG).show()
                idTaskDeleted.postValue(null)
            }
        return idTaskDeleted
    }

    fun listTasks(taskFilterCompleted: Boolean): MutableLiveData<List<TaskEntity>> {
        val tasksList = mutableListOf<TaskEntity>()
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!
        val liveDataTaskList: MutableLiveData<List<TaskEntity>> = MutableLiveData()

        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.AUTHENTICATION_ID, userId)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED, taskFilterCompleted).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val taskEntity = TaskEntity(
                        document.id,
                        userId,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID).toString(),
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION).toString(),
                        document.getBoolean(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED)!!,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE).toString())
                    tasksList.add(taskEntity)
                    liveDataTaskList.postValue(tasksList)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, context.getString(R.string.error_getting_all_tasks_log), exception)
                liveDataTaskList.postValue(null)
            }
        return liveDataTaskList
    }

    fun updateTaskStatus(taskCompleted: Boolean, id: String): MutableLiveData<String> {

        val idTaskUpdate: MutableLiveData<String> = MutableLiveData()

        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(id)
            .update(
                mapOf(
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED to taskCompleted
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, context.getString(R.string.task_updated_message) + id)
                Toast.makeText(context, context.getString(R.string.task_updated_message), Toast.LENGTH_LONG)
                    .show()
                idTaskUpdate.postValue(id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, context.getString(R.string.error_update_task), e)
                Toast.makeText(context, context.getText(R.string.error_update_task), Toast.LENGTH_LONG).show()
                idTaskUpdate.postValue(null)
            }
        return idTaskUpdate
    }
}