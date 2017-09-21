package com.plweegie.android.squash.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by jan on 17/09/17.
 */

@Database(entities = {RepoEntry.class}, version = 1)
public abstract class RepoDatabase extends RoomDatabase{

    private static final String DATABASE_NAME = "repos";
    private static final Object LOCK = new Object();

    public abstract RepoDao repoDao();

    private static volatile RepoDatabase sInstance;

    public static RepoDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context, RepoDatabase.class,
                        RepoDatabase.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }
}
