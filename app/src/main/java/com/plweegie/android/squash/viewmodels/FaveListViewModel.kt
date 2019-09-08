package com.plweegie.android.squash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.plweegie.android.squash.data.RepoEntry
import com.plweegie.android.squash.data.RepoRepository

class FaveListViewModel(private val repository: RepoRepository) : ViewModel() {

    val faveList: LiveData<List<RepoEntry>> = repository.allFavorites

    fun deleteRepo(repoId: Long) {
        repository.deleteRepo(repoId)
    }

    fun deleteAllRepos() {
        repository.deleteAllRepos()
    }
}
