package com.helmut.data.repository

import com.helmut.data.local.TemplateDao
import com.helmut.data.model.Template
import com.helmut.data.model.TemplateTask
import com.helmut.data.model.TemplateWithTasks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepository @Inject constructor(
    private val templateDao: TemplateDao
) {
    
    fun getAllTemplatesWithTasks(): Flow<List<TemplateWithTasks>> {
        return templateDao.getAllTemplatesWithTasks()
    }
    
    suspend fun getTemplateWithTasks(templateId: Long): TemplateWithTasks? {
        return templateDao.getTemplateWithTasks(templateId)
    }
    
    suspend fun createTemplate(template: Template, tasks: List<TemplateTask>): Long {
        val templateId = templateDao.insertTemplate(template)
        val tasksWithTemplateId = tasks.map { it.copy(templateId = templateId) }
        templateDao.insertTemplateTasks(tasksWithTemplateId)
        return templateId
    }
    
    suspend fun updateTemplate(template: Template, tasks: List<TemplateTask>) {
        templateDao.updateTemplate(template)
        templateDao.deleteTemplateTasks(template.id)
        templateDao.insertTemplateTasks(tasks)
    }
    
    suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template)
    }
}
