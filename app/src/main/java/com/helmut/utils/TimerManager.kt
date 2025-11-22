package com.helmut.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TimerState(
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false
)

class TimerManager(private val scope: CoroutineScope) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    private var timerJob: Job? = null
    private var onTimerComplete: (() -> Unit)? = null

    fun startTimer(minutes: Int, onComplete: () -> Unit) {
        val totalSeconds = minutes * 60
        this.onTimerComplete = onComplete
        
        _timerState.value = TimerState(
            remainingSeconds = totalSeconds,
            totalSeconds = totalSeconds,
            isRunning = true,
            isPaused = false
        )
        
        startCountdown()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            isPaused = true
        )
    }

    fun resumeTimer() {
        _timerState.value = _timerState.value.copy(
            isRunning = true,
            isPaused = false
        )
        startCountdown()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
        onTimerComplete = null
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = _timerState.value.remainingSeconds - 1
                )
            }
            
            if (_timerState.value.remainingSeconds == 0) {
                onTimerComplete?.invoke()
                _timerState.value = TimerState()
            }
        }
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
}
