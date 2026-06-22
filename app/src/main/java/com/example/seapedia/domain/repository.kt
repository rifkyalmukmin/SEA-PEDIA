package com.example.seapedia.domain

import com.example.seapedia.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(name: String, email: String, password: String, role: String): Resource<User>
    suspend fun googleSignIn(idToken: String, role: String = "buyer"): Resource<User>
    suspend fun logout()
    fun getAuthState(): Flow<User?>
}
