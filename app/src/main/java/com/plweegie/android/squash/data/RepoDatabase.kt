package com.plweegie.android.squash.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RepoEntry::class], version = 2)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao
}
