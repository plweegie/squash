package com.plweegie.android.squash.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RepoDao {

    @get:Query("SELECT * FROM repos")
    val favorites: LiveData<List<RepoEntry>>

    @Query("SELECT * FROM repos")
    suspend fun getFavoritesAsync(): List<RepoEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(repo: RepoEntry)

    @Query("DELETE FROM repos WHERE repoId = :repoId")
    suspend fun deleteSelected(repoId: Long)

    @Query("DELETE FROM repos")
    suspend fun deleteAll()
}
