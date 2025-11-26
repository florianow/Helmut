package com.helmut.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.helmut.MainActivity
import com.helmut.R
import com.helmut.service.TimerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "helmut_timer_channel"
        private const val COMPLETE_CHANNEL_ID = "helmut_complete_channel"
        const val TIMER_NOTIFICATION_ID = 1
        private const val COMPLETE_NOTIFICATION_ID = 2
    }

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val timerChannel = NotificationChannel(
            CHANNEL_ID,
            "Timer Progress",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows timer progress while running"
            setSound(null, null)
            enableVibration(false)
        }
        
        val completeChannel = NotificationChannel(
            COMPLETE_CHANNEL_ID,
            "Timer Complete",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task timer completion"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(timerChannel)
        notificationManager.createNotificationChannel(completeChannel)
    }

    fun createTimerNotification(
        taskTitle: String,
        timeRemaining: String,
        isPaused: Boolean
    ): Notification {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(context, TimerService::class.java).apply {
            action = if (isPaused) TimerService.ACTION_RESUME_TIMER else TimerService.ACTION_PAUSE_TIMER
        }
        val pauseResumePendingIntent = PendingIntent.getService(
            context, 1, pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP_TIMER
        }
        val stopPendingIntent = PendingIntent.getService(
            context, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = if (isPaused) "Paused" else "Running"
        val pauseResumeIcon = if (isPaused) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground
        val pauseResumeText = if (isPaused) "Resume" else "Pause"

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(taskTitle)
            .setContentText("$timeRemaining - $statusText")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(openPendingIntent)
            .addAction(pauseResumeIcon, pauseResumeText, pauseResumePendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()
    }

    fun updateNotification(notificationId: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, notification)
            } catch (e: SecurityException) {
            }
        }
    }

    fun showTimerCompleteNotification(
        taskTitle: String,
        soundUri: Uri? = null,
        enableVibration: Boolean = true
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val actualSoundUri = soundUri ?: defaultSoundUri

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, COMPLETE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time's Up!")
            .setContentText("$taskTitle is complete")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setSound(actualSoundUri)

        if (enableVibration) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        }

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(COMPLETE_NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
            }
        }

        if (enableVibration) {
            vibrateDevice()
        }
    }

    private fun vibrateDevice() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 250, 500),
                    intArrayOf(0, 255, 0, 255),
                    -1
                )
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 500, 250, 500), -1)
            }
        } catch (e: Exception) {
        }
    }

    fun getAvailableSounds(): List<SoundOption> {
        return listOf(
            SoundOption("Default", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)),
            SoundOption("Alarm", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)),
            SoundOption("Ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        )
    }
}

data class SoundOption(
    val name: String,
    val uri: Uri
)
