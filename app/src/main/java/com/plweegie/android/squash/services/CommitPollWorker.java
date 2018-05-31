package com.plweegie.android.squash.services;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.work.Worker;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class CommitPollWorker extends Worker {

    private GitHubService mService;
    private QueryPreferences mQueryPrefs;
    private RepoRepository mDataRepository;

    private Disposable mDisposable;
    private List<Commit> mCommits;

    @Inject
    public CommitPollWorker(GitHubService service, QueryPreferences queryPrefs,
                            RepoRepository dataRepository) {
        mService = service;
        mQueryPrefs = queryPrefs;
        mDataRepository = dataRepository;

        mCommits = new ArrayList<>();
    }

    @NonNull
    @Override
    public WorkerResult doWork() {
        List<RepoEntry> repos = mDataRepository.getAllFavoritesDirectly();

        String authToken = mQueryPrefs.getStoredAccessToken();

        mDisposable = Observable.fromIterable(repos)
                .flatMap(repoEntry -> mService.getCommits(repoEntry.getOwner().getLogin(),
                        repoEntry.getName(), 1, authToken))
                .subscribeWith(new DisposableObserver<List<Commit>>() {
                    @Override
                    public void onNext(List<Commit> commits) {
                        Commit commit = commits.get(0);
                        mCommits.add(commit);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Crashlytics.log("Error checking for new commits");
                        Crashlytics.logException(e);
                    }

                    @Override
                    public void onComplete() {}
                });
        return WorkerResult.SUCCESS;
    }
}
