package com.plweegie.android.squash.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {RepoEntry.class}, version = 2)
public abstract class RepoDatabase extends RoomDatabase{

    public abstract RepoDao repoDao();
}
