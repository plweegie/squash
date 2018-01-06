package com.plweegie.android.squash.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {RepoEntry.class}, version = 2)
public abstract class RepoDatabase extends RoomDatabase{

    public abstract RepoDao repoDao();
//    private static final Object LOCK = new Object();
//
//    public abstract RepoDao repoDao();
//
//    private static volatile RepoDatabase sInstance;
//
//    public static RepoDatabase getInstance(Context context) {
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = Room.databaseBuilder(context, RepoDatabase.class,
//                        RepoDatabase.DATABASE_NAME).build();
//            }
//        }
//        return sInstance;
//    }
}
