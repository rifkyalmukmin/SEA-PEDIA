package com.example.seapedia.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface SeaPediaApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
