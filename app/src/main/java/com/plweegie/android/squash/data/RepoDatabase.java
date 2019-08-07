package com.plweegie.android.squash.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RepoEntry.class}, version = 2)
public abstract class RepoDatabase extends RoomDatabase{

    public abstract RepoDao repoDao();
}
