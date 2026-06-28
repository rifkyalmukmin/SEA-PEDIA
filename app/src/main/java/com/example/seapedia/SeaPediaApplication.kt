package com.example.seapedia

import android.app.Application
import com.example.seapedia.core.network.ApiClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SeaPediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize API client with application context
        ApiClient.init(this)
    }
}