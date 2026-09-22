package com.example.reminderapp

import android.content.Context

object Prefs {
    private const val NAME = "reminder_prefs"
    private const val KEY_GLOBAL_BG = "global_background_uri"
    private const val KEY_DAILY_COUNT = "daily_reminder_count"
    private const val KEY_WINDOW_START = "window_start_hour" // 0-23
    private const val KEY_WINDOW_END = "window_end_hour"     // 0-23

    fun setGlobalBackground(context: Context, uri: String?) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_GLOBAL_BG, uri).apply()
    }

    fun getGlobalBackground(context: Context): String? =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(KEY_GLOBAL_BG, null)

    // "A few times a day" default = 4, adjustable 1-8 in Settings
    fun setDailyCount(context: Context, count: Int) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_DAILY_COUNT, count).apply()
    }

    fun getDailyCount(context: Context): Int =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(KEY_DAILY_COUNT, 4)

    // Active hours window so reminders don't fire while asleep, e.g. 9am - 10pm
    fun setWindow(context: Context, startHour: Int, endHour: Int) {
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_WINDOW_START, startHour)
            .putInt(KEY_WINDOW_END, endHour)
            .apply()
    }

    fun getWindowStart(context: Context): Int =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(KEY_WINDOW_START, 9)

    fun getWindowEnd(context: Context): Int =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(KEY_WINDOW_END, 22)
}
