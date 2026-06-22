package com.example.seapedia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val phone: String = "",
    val avatarUrl: String = ""
)
