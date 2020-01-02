package com.plweegie.android.squash.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plweegie.android.squash.data.RepoEntry
import com.plweegie.android.squash.data.RepoRepository
import kotlinx.coroutines.launch


class RepoListViewModel(private val repository: RepoRepository) : ViewModel() {

    sealed class LoadingState {
        object Loading : LoadingState()
        class Failed(val exception: Exception) : LoadingState()
        class Succeeded(val repos: List<RepoEntry>?) : LoadingState()
    }

    val loadingState: MutableLiveData<LoadingState> = MutableLiveData()

    fun fetchData(userName: String, page: Int) {
        loadingState.value = LoadingState.Loading

        launchInScope {
            try {
                val result = repository.fetchRepos(userName, page)
                loadingState.postValue(LoadingState.Succeeded(result))
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.Failed(e))
            }
        }
    }

    fun addFavorite(repo: RepoEntry) {
        launchInScope { repository.addFavorite(repo) }
    }

    private fun launchInScope(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }


}