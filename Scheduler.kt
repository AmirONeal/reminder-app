package com.example.reminderapp

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Picks a handful of random times within today's active window (e.g. 9am-10pm)
 * and schedules one ReminderWorker job for each. Call rescheduleToday() once at
 * app start and again each time the daily job runs, so tomorrow gets fresh random times.
 */
object Scheduler {

    private const val TAG_PREFIX = "reminder_job_"

    fun rescheduleToday(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Clear any previously queued jobs for today before laying down new ones
        workManager.cancelAllWorkByTag(TAG_PREFIX + "today")

        val count = Prefs.getDailyCount(context) // "a few times a day", default 4
        val startHour = Prefs.getWindowStart(context)
        val endHour = Prefs.getWindowEnd(context)

        val now = Calendar.getInstance()
        val windowStart = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, startHour); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }
        val windowEnd = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, endHour); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }

        // If we're already past today's window, schedule for tomorrow's window instead
        if (now.after(windowEnd)) {
            windowStart.add(Calendar.DAY_OF_YEAR, 1)
            windowEnd.add(Calendar.DAY_OF_YEAR, 1)
        }

        val rangeMillis = windowEnd.timeInMillis - windowStart.timeInMillis
        val effectiveStart = maxOf(windowStart.timeInMillis, now.timeInMillis)

        repeat(count) {
            val remainingRange = windowEnd.timeInMillis - effectiveStart
            if (remainingRange <= 0) return@repeat
            val randomOffset = Random.nextLong(remainingRange)
            val fireAt = effectiveStart + randomOffset
            val delay = fireAt - System.currentTimeMillis()
            if (delay <= 0) return@repeat

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(TAG_PREFIX + "today")
                .build()
            workManager.enqueue(request)
        }

        // Queue tomorrow's re-scheduling job to run shortly after midnight
        val midnightTomorrow = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 5); set(Calendar.SECOND, 0)
        }
        val delayToMidnight = midnightTomorrow.timeInMillis - System.currentTimeMillis()
        val rescheduleRequest = OneTimeWorkRequestBuilder<DailyRescheduleWorker>()
            .setInitialDelay(delayToMidnight, TimeUnit.MILLISECONDS)
            .addTag(TAG_PREFIX + "reschedule")
            .build()
        workManager.cancelAllWorkByTag(TAG_PREFIX + "reschedule")
        workManager.enqueue(rescheduleRequest)
    }
}
