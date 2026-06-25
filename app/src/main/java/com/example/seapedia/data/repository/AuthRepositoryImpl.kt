package com.example.seapedia.data.repository

import com.example.seapedia.core.utils.Resource
import com.example.seapedia.data.local.SeaPediaDatabase
import com.example.seapedia.data.local.SessionManager
import com.example.seapedia.data.local.UserEntity
import com.example.seapedia.data.remote.GoogleAuthRequest
import com.example.seapedia.data.remote.LoginRequest
import com.example.seapedia.data.remote.RegisterRequest
import com.example.seapedia.data.remote.SeaPediaApi
import com.example.seapedia.data.remote.UserData
import com.example.seapedia.domain.AuthRepository
import com.example.seapedia.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: SeaPediaApi,
    private val db: SeaPediaDatabase,
    private val session: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> = try {
        val response = api.login(LoginRequest(email, password))
        if (response.success && response.data != null) {
            val user = response.data.toDomain()
            db.userDao().insertUser(user.toEntity())
            saveSession(user)
            Resource.Success(user)
        } else {
            Resource.Error(response.message)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Login failed")
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ): Resource<User> = try {
        val response = api.register(RegisterRequest(name, email, password, role))
        if (response.success && response.data != null) {
            val user = response.data.toDomain()
            db.userDao().insertUser(user.toEntity())
            saveSession(user)
            Resource.Success(user)
        } else {
            Resource.Error(response.message)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Register failed")
    }

    override suspend fun googleSignIn(idToken: String, role: String): Resource<User> = try {
        val response = api.googleAuth(GoogleAuthRequest(idToken, role))
        if (response.success && response.data != null) {
            val user = response.data.toDomain()
            db.userDao().insertUser(user.toEntity())
            saveSession(user)
            Resource.Success(user)
        } else {
            Resource.Error(response.message)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Google Sign-In failed")
    }

    override suspend fun logout() {
        db.userDao().clearUser()
        session.clear()
    }

    override fun getAuthState(): Flow<User?> =
        db.userDao().getUser().map { it?.toDomain() }

    private suspend fun saveSession(user: User) {
        session.saveSession(token = user.token, role = user.role, userId = user.id)
    }
}

private fun UserData.toDomain() = User(
    id = id, name = name, email = email, role = role,
    token = token, phone = phone, avatarUrl = avatarUrl
)

private fun User.toEntity() = UserEntity(
    id = id, name = name, email = email, role = role,
    token = token, phone = phone, avatarUrl = avatarUrl
)

private fun UserEntity.toDomain() = User(
    id = id, name = name, email = email, role = role,
    token = token, phone = phone, avatarUrl = avatarUrl
)
