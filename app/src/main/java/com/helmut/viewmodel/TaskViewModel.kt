package com.helmut.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helmut.data.model.Task
import com.helmut.data.repository.SettingsRepository
import com.helmut.data.repository.TaskRepository
import com.helmut.service.TimerService
import com.helmut.utils.NotificationHelper
import com.helmut.utils.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val activeTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: TaskRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var timerService: TimerService? = null
    private var bound = false

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            bound = true
            
            viewModelScope.launch {
                timerService?.getTimerState()?.collect { state ->
                    _timerState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            timerService = null
        }
    }

    init {
        loadTasks()
        loadCompletedTasks()
        bindTimerService()
    }

    private fun bindTimerService() {
        Intent(context, TimerService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getActiveTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    activeTasks = tasks,
                    currentTask = tasks.firstOrNull()
                )
            }
        }
    }

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            repository.getCompletedTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    completedTasks = tasks
                )
            }
        }
    }

    fun addTask(title: String, estimatedMinutes: Int) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                estimatedMinutes = estimatedMinutes,
                order = _uiState.value.activeTasks.size
            )
            repository.addTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            repository.completeTask(task)
            stopTimer()
        }
    }

    fun startTask(task: Task) {
        _uiState.value = _uiState.value.copy(currentTask = task)
        
        viewModelScope.launch {
            val notificationEnabled = settingsRepository.notificationEnabled.first()
            val vibrationEnabled = settingsRepository.vibrationEnabled.first()
            val soundUri = settingsRepository.notificationSoundUri.first()
            
            val intent = Intent(context, TimerService::class.java).apply {
                action = TimerService.ACTION_START_TIMER
                putExtra(TimerService.EXTRA_TASK_TITLE, task.title)
                putExtra(TimerService.EXTRA_MINUTES, task.estimatedMinutes)
            }
            context.startForegroundService(intent)
            
            timerService?.startTimer(task.title, task.estimatedMinutes) {
                viewModelScope.launch {
                    if (notificationEnabled) {
                        notificationHelper.showTimerCompleteNotification(
                            taskTitle = task.title,
                            soundUri = soundUri,
                            enableVibration = vibrationEnabled
                        )
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerService?.pauseTimer()
    }

    fun resumeTimer() {
        timerService?.resumeTimer()
    }

    fun stopTimer() {
        timerService?.stopTimerAndService()
    }

    fun skipTask() {
        stopTimer()
        val tasks = _uiState.value.activeTasks
        val currentIndex = tasks.indexOfFirst { it.id == _uiState.value.currentTask?.id }
        if (currentIndex < tasks.size - 1) {
            _uiState.value = _uiState.value.copy(currentTask = tasks[currentIndex + 1])
        }
    }

    override fun onCleared() {
        if (bound) {
            context.unbindService(serviceConnection)
            bound = false
        }
        super.onCleared()
    }
}
