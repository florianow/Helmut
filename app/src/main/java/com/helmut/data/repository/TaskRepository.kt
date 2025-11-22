package com.helmut.data.repository

import com.helmut.data.local.TaskDao
import com.helmut.data.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()

    suspend fun getTaskById(taskId: Long): Task? = taskDao.getTaskById(taskId)

    suspend fun addTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun completeTask(task: Task) {
        taskDao.updateTask(
            task.copy(
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteAllCompletedTasks() = taskDao.deleteAllCompletedTasks()
}
