package com.example.seapedia.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.seapedia.core.utils.Constants
import com.example.seapedia.core.utils.Resource
import com.example.seapedia.data.local.SeaPediaDatabase
import com.example.seapedia.data.local.UserEntity
import com.example.seapedia.data.remote.LoginRequest
import com.example.seapedia.data.remote.RegisterRequest
import com.example.seapedia.data.remote.SeaPediaApi
import com.example.seapedia.data.remote.UserData
import com.example.seapedia.domain.AuthRepository
import com.example.seapedia.domain.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

class AuthRepositoryImpl @Inject constructor(
    private val api: SeaPediaApi,
    private val db: SeaPediaDatabase,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val tokenKey = stringPreferencesKey(Constants.KEY_USER_TOKEN)
    private val roleKey = stringPreferencesKey(Constants.KEY_USER_ROLE)
    private val userIdKey = stringPreferencesKey(Constants.KEY_USER_ID)

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

    override suspend fun logout() {
        db.userDao().clearUser()
        context.dataStore.edit { it.clear() }
    }

    override fun getAuthState(): Flow<User?> =
        db.userDao().getUser().map { it?.toDomain() }

    private suspend fun saveSession(user: User) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = user.token
            prefs[roleKey] = user.role
            prefs[userIdKey] = user.id
        }
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
