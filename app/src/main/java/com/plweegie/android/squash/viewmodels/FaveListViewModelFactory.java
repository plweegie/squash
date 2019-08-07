package com.plweegie.android.squash.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.plweegie.android.squash.data.RepoRepository;

public class FaveListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final RepoRepository mRepository;

    public FaveListViewModelFactory(RepoRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FaveListViewModel(mRepository);
    }
}
