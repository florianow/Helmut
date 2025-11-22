package com.helmut.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.helmut.data.model.Template
import com.helmut.data.model.TemplateTask
import com.helmut.data.model.TemplateWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    
    @Transaction
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun getAllTemplatesWithTasks(): Flow<List<TemplateWithTasks>>
    
    @Transaction
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateWithTasks(templateId: Long): TemplateWithTasks?
    
    @Insert
    suspend fun insertTemplate(template: Template): Long
    
    @Insert
    suspend fun insertTemplateTasks(tasks: List<TemplateTask>)
    
    @Update
    suspend fun updateTemplate(template: Template)
    
    @Delete
    suspend fun deleteTemplate(template: Template)
    
    @Query("DELETE FROM template_tasks WHERE templateId = :templateId")
    suspend fun deleteTemplateTasks(templateId: Long)
    
    @Query("SELECT * FROM template_tasks WHERE templateId = :templateId ORDER BY `order` ASC")
    suspend fun getTasksForTemplate(templateId: Long): List<TemplateTask>
}
