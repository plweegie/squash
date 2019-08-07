package com.plweegie.android.squash.data;


import android.app.Application;
import androidx.room.Room;

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
