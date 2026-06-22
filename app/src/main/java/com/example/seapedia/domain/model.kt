package com.example.seapedia.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val phone: String = "",
    val avatarUrl: String = ""
)
