package com.plweegie.android.squash.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RepoDao {

    @Query("SELECT * FROM repos")
    LiveData<List<RepoEntry>> getFavorites();

    @Query("SELECT * FROM repos")
    List<RepoEntry> getFavoritesDirectly();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(RepoEntry repo);

    @Query("DELETE FROM repos WHERE repoId = :repoId")
    void deleteSelected(long repoId);

    @Query("DELETE FROM repos")
    void deleteAll();
}
