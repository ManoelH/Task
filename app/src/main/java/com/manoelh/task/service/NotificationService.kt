package com.manoelh.task.service

import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.os.Handler
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


class NotificationService : Service(){

    private val CHANNEL_ID = "taskChannel_id"
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private lateinit var TAG: String
    private val ONE_HOUR_IN_MILISECONDS_TO_SEARCH_TASKS = 3600000L
    private val taskCompleted = false


    private val db = FirebaseFirestore.getInstance()
    private lateinit var mSecurityPreferences: SecurityPreferences
    private var mTasksPending: MutableList<TaskEntity> = arrayListOf()

    override fun onCreate() {
        initializeVariables()
        Log.e(TAG, "onCreate notificationService")
    }

    private fun initializeVariables() {
        TAG = this.getString(R.string.app_name)
        mSecurityPreferences = SecurityPreferences(this)
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy notificationService")
        stopTimerTask()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand notificationService")
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    private fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer!!.schedule(timerTask, 5000, ONE_HOUR_IN_MILISECONDS_TO_SEARCH_TASKS)
    }

    val handler: Handler = Handler()

    private fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post { listTasks() }
            }
        }
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

        mTasksPending.forEach {
            val dueDate = it.dueDate
            val dateFormatter = DateTimeFormatter.ofPattern(TaskConstants.DATE.PATTERN)
            val date = LocalDate.parse(dueDate, dateFormatter)
            if (date.dayOfYear == calendar[Calendar.DAY_OF_YEAR] && date.year == calendar[Calendar.YEAR]){
                buildingNotification(it.description, it.dueDate)
                Thread.sleep(3000)
            }
        }
    }

    private fun buildingNotification(taskDescription: String, dueDate: String) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mail)
            .setContentTitle(taskDescription)
            .setContentText("It's now complete your task, due date: $dueDate")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, notification.build())
        }
    }
}