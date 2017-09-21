package com.plweegie.android.squash.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.plweegie.android.squash.data.RepoRepository;

/**
 * Created by jan on 18/09/17.
 */

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
