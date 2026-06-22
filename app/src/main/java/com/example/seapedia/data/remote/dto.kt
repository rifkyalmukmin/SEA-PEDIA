package com.example.seapedia.data.remote

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class GoogleAuthRequest(
    val idToken: String,
    val role: String = "buyer"
)
