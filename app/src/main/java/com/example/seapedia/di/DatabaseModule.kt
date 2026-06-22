package com.example.seapedia.di

import android.content.Context
import androidx.room.Room
import com.example.seapedia.data.local.SeaPediaDatabase
import com.example.seapedia.core.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SeaPediaDatabase =
        Room.databaseBuilder(
            context,
            SeaPediaDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
}
