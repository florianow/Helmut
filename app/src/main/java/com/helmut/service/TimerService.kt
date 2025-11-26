package com.helmut.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.helmut.utils.NotificationHelper
import com.helmut.utils.TimerManager
import com.helmut.utils.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var timerManager: TimerManager
    
    private val binder = TimerBinder()
    
    private var currentTaskTitle: String = ""
    private var onTimerComplete: (() -> Unit)? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        timerManager = TimerManager(serviceScope)
        
        serviceScope.launch {
            timerManager.timerState.collect { state ->
                if (state.isRunning) {
                    updateNotification(state)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: ""
                val minutes = intent.getIntExtra(EXTRA_MINUTES, 15)
                startTimer(taskTitle, minutes)
            }
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_RESUME_TIMER -> resumeTimer()
            ACTION_STOP_TIMER -> stopTimerAndService()
        }
        return START_STICKY
    }

    fun startTimer(taskTitle: String, minutes: Int, onComplete: (() -> Unit)? = null) {
        this.currentTaskTitle = taskTitle
        this.onTimerComplete = onComplete
        
        val notification = notificationHelper.createTimerNotification(
            taskTitle = taskTitle,
            timeRemaining = timerManager.formatTime(minutes * 60),
            isPaused = false
        )
        
        startForeground(NotificationHelper.TIMER_NOTIFICATION_ID, notification)
        
        timerManager.startTimer(minutes) {
            handleTimerComplete()
        }
    }

    fun pauseTimer() {
        timerManager.pauseTimer()
        updateNotification(timerManager.timerState.value)
    }

    fun resumeTimer() {
        timerManager.resumeTimer()
        updateNotification(timerManager.timerState.value)
    }

    fun stopTimerAndService() {
        timerManager.stopTimer()
        stopForegroundService()
    }

    fun getTimerState(): StateFlow<TimerState> = timerManager.timerState

    private fun updateNotification(state: TimerState) {
        val notification = notificationHelper.createTimerNotification(
            taskTitle = currentTaskTitle,
            timeRemaining = timerManager.formatTime(state.remainingSeconds),
            isPaused = state.isPaused
        )
        notificationHelper.updateNotification(NotificationHelper.TIMER_NOTIFICATION_ID, notification)
    }

    private fun handleTimerComplete() {
        onTimerComplete?.invoke()
        notificationHelper.showTimerCompleteNotification(currentTaskTitle)
        stopForegroundService()
    }

    private fun stopForegroundService() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_TIMER = "com.helmut.action.START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.helmut.action.PAUSE_TIMER"
        const val ACTION_RESUME_TIMER = "com.helmut.action.RESUME_TIMER"
        const val ACTION_STOP_TIMER = "com.helmut.action.STOP_TIMER"
        
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_MINUTES = "minutes"
    }
}
