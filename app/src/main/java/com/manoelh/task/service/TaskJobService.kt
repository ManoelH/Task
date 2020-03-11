package com.manoelh.task.service

import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.activity.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "JobService"
private const val taskCompleted = false

class TaskJobService: JobService(), Runnable {

    private var jobCancelled = false
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mSecurityPreferences: SecurityPreferences
    private var mTasksPending: MutableList<TaskEntity> = arrayListOf()


    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: IBinder? = null        // interface for clients that bind
    private var allowRebind: Boolean = false
    private lateinit var mPhotoTask: TaskRunnableDecodeMethods

    interface TaskRunnableDecodeMethods {
        fun setImageDecodeThread(currentThread: Thread?)
        val byteBuffer: ByteArray?

        fun handleDecodeState(state: Int)
        val targetWidth: Int
        val targetHeight: Int

        fun setImage(image: Bitmap?)
    }

    override fun run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        mPhotoTask.setImageDecodeThread(Thread.currentThread())

        //TEST
        Thread(Runnable {
            run {
                Log.d(TAG, "LISTING TASKS PENDING")
                listTasks()
                if (jobCancelled)
                    return@run

                Log.d(TAG, "JOB FINISHED")
                jobFinished(params, false)
            }
        }).start()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        mSecurityPreferences = SecurityPreferences(this)
        Log.d(TAG, "JOB STARTED")
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?){
        Thread(Runnable {
            run {
                Log.d(TAG, "LISTING TASKS PENDING")
                listTasks()
                if (jobCancelled)
                    return@run

                Log.d(TAG, "JOB FINISHED")
                jobFinished(params, false)
            }
        }).start()
    }

    private fun listTasks() {
        val tasksList = mutableListOf<TaskEntity>()
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!

        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.AUTHENTICATION_ID, userId)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED, taskCompleted).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    val taskEntity = TaskEntity(
                        document.id,
                        userId,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID).toString(),
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION).toString(),
                        document.getBoolean(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED)!!,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE).toString())
                    tasksList.add(taskEntity)
                }
                mTasksPending = tasksList
                verifyIfExistsSomeTasksPendingToThisDate()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, getString(R.string.error_getting_all_tasks_log), exception)
            }
    }

    private fun verifyIfExistsSomeTasksPendingToThisDate(){
        val calendar = Calendar.getInstance()
        var idNotification = 1
        mTasksPending.forEach {
            val dueDate = it.dueDate
            val dateFormatter = DateTimeFormatter.ofPattern(TaskConstants.DATE.PATTERN)
            val date = LocalDate.parse(dueDate, dateFormatter)
            if (date.dayOfYear == calendar[Calendar.DAY_OF_YEAR] && date.year == calendar[Calendar.YEAR]){
                buildingNotification(it.description, it.dueDate, idNotification)
                Thread.sleep(3000)
            }
            idNotification++
        }
    }

    private fun buildingNotification(taskDescription: String, dueDate: String, idNotification: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mail)
            .setContentTitle(taskDescription)
            .setContentText("It's now complete your task! Due date: $dueDate")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setChannelId(TaskConstants.CHANNEL_ID.TASK_PENDING)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(idNotification, notification.build())
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "JOB CANCELLED BEFORE COMPLETION")
        jobCancelled = true
        return true
    }
}