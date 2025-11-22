package com.helmut.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.helmut.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "helmut_timer_channel"
        private const val NOTIFICATION_ID = 1
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
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Timer Notifications"
        val descriptionText = "Notifications for task timer completion"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showTimerCompleteNotification(
        taskTitle: String,
        soundUri: Uri? = null,
        enableVibration: Boolean = true
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val actualSoundUri = soundUri ?: defaultSoundUri

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time's Up!")
            .setContentText("$taskTitle is complete")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(actualSoundUri)

        if (enableVibration) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        }

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
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
