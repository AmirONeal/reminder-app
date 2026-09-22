package com.example.reminderapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
    }

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getInstance(applicationContext).messageDao()
        val message = dao.getRandomMessage() ?: return Result.success() // nothing to send yet

        createChannelIfNeeded()

        val bgUriString = message.backgroundUri ?: Prefs.getGlobalBackground(applicationContext)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(message.text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // If there's a background image, show it as a big picture notification
        if (bgUriString != null) {
            try {
                val uri = Uri.parse(bgUriString)
                applicationContext.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .bigLargeIcon(null as android.graphics.Bitmap?)
                        )
                    }
                }
            } catch (e: Exception) {
                // Background couldn't be loaded (e.g. permission revoked); fall back to plain text
            }
        } else {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message.text))
        }

        val openIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, message.id.toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pendingIntent)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(message.id.toInt(), builder.build())

        return Result.success()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    applicationContext.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }
        }
    }
}
