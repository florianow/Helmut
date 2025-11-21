package com.eaddie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eaddie.data.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
