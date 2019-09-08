package com.plweegie.android.squash.rest


import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class SharedPrefModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(application: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)
}
