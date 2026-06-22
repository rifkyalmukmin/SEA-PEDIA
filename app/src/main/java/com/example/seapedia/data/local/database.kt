package com.example.seapedia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class SeaPediaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
