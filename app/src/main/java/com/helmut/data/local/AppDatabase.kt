package com.helmut.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.helmut.data.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
