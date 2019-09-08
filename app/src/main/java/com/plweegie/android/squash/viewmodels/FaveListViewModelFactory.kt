package com.plweegie.android.squash.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.plweegie.android.squash.data.RepoRepository
import javax.inject.Inject
import javax.inject.Provider

class FaveListViewModelFactory @Inject constructor(private val repository: Provider<RepoRepository>) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FaveListViewModel(repository.get()) as T
}
