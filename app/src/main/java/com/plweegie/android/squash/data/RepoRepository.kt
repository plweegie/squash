package com.plweegie.android.squash.data

import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(database: RepoDatabase,
                                         private val executor: ExecutorService) {

    private val repoDao: RepoDao = database.repoDao()

    val allFavorites: LiveData<List<RepoEntry>>
        get() = repoDao.favorites

    suspend fun getFavoritesAsync() = repoDao.getFavoritesAsync()

    suspend fun addFavorite(repo: RepoEntry) {
       repoDao.insertFavorite(repo)
    }

    suspend fun deleteRepo(repoId: Long) {
       repoDao.deleteSelected(repoId)
    }

    suspend fun deleteAllRepos() {
        repoDao.deleteAll()
    }
}
