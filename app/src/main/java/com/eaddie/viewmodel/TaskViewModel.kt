package com.eaddie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaddie.data.model.Task
import com.eaddie.data.repository.TaskRepository
import com.eaddie.utils.TimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val activeTasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val timerManager = TimerManager(viewModelScope)

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
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
            timerManager.stopTimer()
        }
    }

    fun startTask(task: Task) {
        _uiState.value = _uiState.value.copy(currentTask = task)
        timerManager.startTimer(task.estimatedMinutes) {
            viewModelScope.launch {
            }
        }
    }

    fun pauseTimer() {
        timerManager.pauseTimer()
    }

    fun resumeTimer() {
        timerManager.resumeTimer()
    }

    fun skipTask() {
        timerManager.stopTimer()
        val tasks = _uiState.value.activeTasks
        val currentIndex = tasks.indexOfFirst { it.id == _uiState.value.currentTask?.id }
        if (currentIndex < tasks.size - 1) {
            _uiState.value = _uiState.value.copy(currentTask = tasks[currentIndex + 1])
        }
    }
}
