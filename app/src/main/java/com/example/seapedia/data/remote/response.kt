package com.example.seapedia.data.remote

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val phone: String = "",
    val avatarUrl: String = ""
)
