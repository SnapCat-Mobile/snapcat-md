package com.snapcat.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.snapcat.data.model.Cat

@Database(entities = [Cat::class], version = 1, exportSchema = false)
abstract class SnapCatDatabase : RoomDatabase() {
    abstract fun snapCatDao(): SnapCatDao
}