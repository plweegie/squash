package com.plweegie.android.squash.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;

import java.util.List;

public class FaveListViewModel extends ViewModel {

    private final LiveData<List<RepoEntry>> mFaveList;
    private final RepoRepository mRepository;

    public FaveListViewModel(RepoRepository repository) {
        mRepository = repository;
        mFaveList = mRepository.getAllFavorites();
    }

    public LiveData<List<RepoEntry>> getFaveList() {
        return mFaveList;
    }

}
