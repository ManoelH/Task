package com.manoelh.task.service

import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.manoelh.task.R
import com.manoelh.task.views.activity.MainActivity
import java.util.*


class NotificationService : Service(){

    private val CHANNEL_ID = "taskChannel_id"
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Tasks"
    var Your_X_SECS = 10


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }


    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        stopTimerTask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler: Handler = Handler()

    fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer!!.schedule(timerTask, 5000, Your_X_SECS * 1000.toLong()) //
    }

    fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post(Runnable { buildingNotification() })
            }
        }
    }

    private fun buildingNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mail)
            .setContentTitle("Task")
            .setContentText("It's the day of your Task!")
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