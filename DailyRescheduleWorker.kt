package com.example.reminderapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyRescheduleWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Scheduler.rescheduleToday(applicationContext)
        return Result.success()
    }
}
