package com.plweegie.android.squash.modules;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.plweegie.android.squash.data.RepoDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    String mDatabaseName;

    public RoomModule(String databaseName) { this.mDatabaseName = databaseName;}

    @Provides
    @Singleton
    RepoDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, RepoDatabase.class, this.mDatabaseName).build();
    }
}
