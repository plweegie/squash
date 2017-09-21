package com.plweegie.android.squash.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;

import java.util.List;

/**
 * Created by jan on 18/09/17.
 */

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
