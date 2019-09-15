package com.plweegie.android.squash.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.plweegie.android.squash.data.RepoEntry
import com.plweegie.android.squash.data.RepoRepository
import kotlinx.coroutines.launch

class FaveListViewModel(private val repository: RepoRepository) : ViewModel() {

    val faveList: LiveData<List<RepoEntry>> = repository.allFavorites

    fun deleteRepo(repoId: Long) {
        viewModelScope.launch {
            repository.deleteRepo(repoId)
        }
    }

    fun deleteAllRepos() {
        viewModelScope.launch {
            repository.deleteAllRepos()
        }
    }
}
