package com.helmut.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helmut.data.model.Task
import com.helmut.data.model.Template
import com.helmut.data.model.TemplateTask
import com.helmut.data.model.TemplateWithTasks
import com.helmut.data.repository.TaskRepository
import com.helmut.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val templates: StateFlow<List<TemplateWithTasks>> = templateRepository
        .getAllTemplatesWithTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createTemplate(name: String, description: String, icon: String, tasks: List<TemplateTask>) {
        viewModelScope.launch {
            val template = Template(
                name = name,
                description = description,
                icon = icon
            )
            templateRepository.createTemplate(template, tasks)
        }
    }

    fun deleteTemplate(template: Template) {
        viewModelScope.launch {
            templateRepository.deleteTemplate(template)
        }
    }

    fun addTemplateToToday(templateWithTasks: TemplateWithTasks) {
        viewModelScope.launch {
            val activeTasks = taskRepository.getActiveTasksList()
            val maxOrder = if (activeTasks.isEmpty()) -1 else activeTasks.maxOf { it.order }
            
            templateWithTasks.tasks.forEachIndexed { index, templateTask ->
                val task = Task(
                    title = templateTask.title,
                    estimatedMinutes = templateTask.estimatedMinutes,
                    order = maxOrder + index + 1
                )
                taskRepository.addTask(task)
            }
        }
    }
    
    fun initializeDefaultTemplates() {
        viewModelScope.launch {
            val existingTemplates = templates.value
            if (existingTemplates.isEmpty()) {
                createTemplate(
                    name = "Morning Routine",
                    description = "Start your day right",
                    icon = "☀️",
                    tasks = listOf(
                        TemplateTask(templateId = 0, title = "Meditation", estimatedMinutes = 10, order = 0),
                        TemplateTask(templateId = 0, title = "Exercise", estimatedMinutes = 30, order = 1),
                        TemplateTask(templateId = 0, title = "Healthy Breakfast", estimatedMinutes = 15, order = 2)
                    )
                )
                
                createTemplate(
                    name = "Deep Work Session",
                    description = "Pomodoro-style focus blocks",
                    icon = "🎯",
                    tasks = listOf(
                        TemplateTask(templateId = 0, title = "Focus Block 1", estimatedMinutes = 25, order = 0),
                        TemplateTask(templateId = 0, title = "Short Break", estimatedMinutes = 5, order = 1),
                        TemplateTask(templateId = 0, title = "Focus Block 2", estimatedMinutes = 25, order = 2),
                        TemplateTask(templateId = 0, title = "Long Break", estimatedMinutes = 15, order = 3)
                    )
                )
                
                createTemplate(
                    name = "Evening Wind Down",
                    description = "Relax and prepare for tomorrow",
                    icon = "🌙",
                    tasks = listOf(
                        TemplateTask(templateId = 0, title = "Review Today", estimatedMinutes = 10, order = 0),
                        TemplateTask(templateId = 0, title = "Plan Tomorrow", estimatedMinutes = 10, order = 1),
                        TemplateTask(templateId = 0, title = "Reading", estimatedMinutes = 20, order = 2)
                    )
                )
            }
        }
    }
}
