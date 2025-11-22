package com.helmut.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.helmut.data.model.Task
import com.helmut.data.model.Template
import com.helmut.data.model.TemplateTask

@Database(
    entities = [Task::class, Template::class, TemplateTask::class], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun templateDao(): TemplateDao
}
