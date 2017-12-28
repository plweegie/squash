package com.plweegie.android.squash.modules;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPrefModule {

    public SharedPrefModule() {}

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
