package com.plweegie.android.squash.data


import android.app.Application
import androidx.room.Room

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
class RoomModule(private val databaseName: String) {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): RepoDatabase =
        Room.databaseBuilder(application, RepoDatabase::class.java, databaseName).build()

    @Provides
    @Singleton
    fun provideDiskIOExecutor(): ExecutorService = Executors.newSingleThreadExecutor()
}
