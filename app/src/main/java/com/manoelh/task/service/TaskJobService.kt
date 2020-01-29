package com.manoelh.task.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class TaskJobService: JobService() {

    private val TAG = "JobService"
    private var jobCancelled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job started")
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?){
        Thread(Runnable {
            run {
                for (i in 0..10){
                    Log.d(TAG, "$i")
                    if (jobCancelled)
                        return@run

                    Thread.sleep(1000)
                }
                Log.d(TAG, "Job finished")
                jobFinished(params, false)
            }
        }).start()

        Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "job cancelled before completion")
        jobCancelled = true
        return true
    }
}