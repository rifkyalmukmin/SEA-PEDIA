package com.example.seapedia.core.network

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.edit
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Singleton class to provide Retrofit instance and API service
 */
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:3000/" // Android emulator localhost
    // For physical device, use your computer's local IP: "http://YOUR_IP:3000/"

    private lateinit var apiService: ApiService
    private lateinit var dataStore: DataStore<Preferences>

    private val DATASTORE_NAME = "user_prefs"
    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_USER_ID = "user_id"

    /**
     * Initialize the API client with application context
     */
    fun init(context: Context) {
        dataStore = context.dataStore(preferencesDataSource { file ->
            file.name = DATASTORE_NAME
        })

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")

                // Add auth token if available
                dataStore.data.first().then { prefs ->
                    val token = prefs[KEY_ACCESS_TOKEN] ?: ""
                    if (token.isNotEmpty()) {
                        newRequest.header("Authorization", "Bearer $token")
                    }
                }.awaitFirst()

                chain.proceed(newRequest.build())
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    /**
     * Get the API service instance
     */
    fun getApiService(): ApiService = apiService

    /**
     * Save authentication tokens and user data
     */
    suspend fun saveAuthData(accessToken: String, refreshToken: String? = null, userId: String? = null) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            refreshToken?.let { prefs[KEY_REFRESH_TOKEN] = it }
            userId?.let { prefs[KEY_USER_ID] = it }
        }
    }

    /**
     * Get saved access token
     */
    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[KEY_ACCESS_TOKEN]
    }

    /**
     * Clear all authentication data (logout)
     */
    suspend fun clearAuthData() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_USER_ID)
        }
    }

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        val token = dataStore.data.first()[KEY_ACCESS_TOKEN]
        return token != null && token.isNotEmpty()
    }

    /**
     * Get user ID from storage
     */
    suspend fun getUserId(): String? {
        return dataStore.data.first()[KEY_USER_ID]
    }
}

/**
 * Custom interceptor to add auth headers
 */
class AuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Authorization header is added in ApiClient.init()
        return chain.proceed(request)
    }
}