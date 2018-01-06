package com.plweegie.android.squash.data;

import android.arch.lifecycle.LiveData;

import com.plweegie.android.squash.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepoRepository {
    // For Singleton instantiation
//    private static final Object LOCK = new Object();
//    private static RepoRepository sInstance;
    private final RepoDao mRepoDao;
    private final AppExecutors mExecutors;

    @Inject
    public RepoRepository(RepoDatabase database, AppExecutors executors) {
        mRepoDao = database.repoDao();
        mExecutors = executors;
    }

//    public synchronized static RepoRepository getInstance(RepoDao repoDao,
//                                                          AppExecutors executors) {
//
//        if (sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = new RepoRepository(repoDao, executors);
//            }
//        }
//        return sInstance;
//    }

    public LiveData<List<RepoEntry>> getAllFavorites() {
        return mRepoDao.getFavorites();
    }

    public List<RepoEntry> getAllFavoritesDirectly() {
        return mRepoDao.getFavoritesDirectly();
    }

    public void addFavorite(RepoEntry repo) {
        mExecutors.diskIO().execute(() -> {
            mRepoDao.insertFavorite(repo);
        });
    }

    public void deleteRepo(long repoId) {
        mExecutors.diskIO().execute(() -> {
            mRepoDao.deleteSelected(repoId);
        });
    }

    public void deleteAllRepos() {
        mExecutors.diskIO().execute(() -> {
            mRepoDao.deleteAll();
        });
    }
}
