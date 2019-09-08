package com.plweegie.android.squash

import android.app.Application

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun providesApplication(): Application = application
}
