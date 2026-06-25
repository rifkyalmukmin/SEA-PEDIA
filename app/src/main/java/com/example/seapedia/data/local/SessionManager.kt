package com.example.seapedia.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.seapedia.core.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

/**
 * Sumber tunggal untuk sesi pengguna (token/role/id) yang disimpan di DataStore.
 *
 * Disentralisasi di sini agar repository dan [com.example.seapedia.data.remote.AuthInterceptor]
 * memakai instance DataStore yang sama — membuat dua delegate `preferencesDataStore` dengan
 * nama sama dalam satu proses akan menyebabkan crash.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey(Constants.KEY_USER_TOKEN)
    private val roleKey = stringPreferencesKey(Constants.KEY_USER_ROLE)
    private val userIdKey = stringPreferencesKey(Constants.KEY_USER_ID)

    val token: Flow<String?> = context.dataStore.data.map { it[tokenKey] }
    val role: Flow<String?> = context.dataStore.data.map { it[roleKey] }
    val userId: Flow<String?> = context.dataStore.data.map { it[userIdKey] }

    suspend fun getToken(): String? = token.first()

    /** Pembacaan token sinkron untuk OkHttp interceptor (berjalan di thread I/O OkHttp). */
    fun getTokenBlocking(): String? = runBlocking { getToken() }

    suspend fun saveSession(token: String, role: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = token
            prefs[roleKey] = role
            prefs[userIdKey] = userId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
