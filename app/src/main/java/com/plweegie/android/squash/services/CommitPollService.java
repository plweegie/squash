/*
 * Copyright (c) 2017 Jan K Szymanski

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package com.plweegie.android.squash.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.plweegie.android.squash.App;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.ui.LastCommitDetailsActivity;
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class CommitPollService extends JobService {
    private static final String TAG = "CommitPollService";
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.plweegie.android.squash.SHOW_NOTIFICATION";
    public static final String PERMISSION_PRIVATE =
            "com.plweegie.android.squash.PRIVATE";
    public static final String REQUEST_CODE = "request_code";
    public static final String NOTIFICATION = "notification";

    @Inject
    GitHubService mService;

    @Inject
    QueryPreferences mQueryPrefs;

    @Inject
    RepoRepository mDataRepository;

    private Disposable mDisposable;
    private CommitPollTask mTask;

    public CommitPollService() {
    }

    @Override
    public void onCreate() {
        ((App) getApplication()).getNetComponent().inject(this);
        super.onCreate();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mTask = new CommitPollTask(this);
        mTask.execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mTask != null) {
            mTask.cancel(true);
        }
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        return true;
    }

    private class CommitPollTask extends AsyncTask<JobParameters, Void, Void> {

        private Context mContext;
        private List<Commit> mCommits;

        public CommitPollTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mCommits = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];

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
                            jobFinished(jobParams, true);
                        }

                        @Override
                        public void onComplete() {
                            jobFinished(jobParams, false);
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            long lastDate = mQueryPrefs.getLastResultDate();
            long newLastDate = 0L;

            Collections.sort(mCommits, new QueryPreferences.CommitCreatedComparator());

            try {
                newLastDate = mCommits.isEmpty() ? lastDate :
                        DateUtils.convertToTimestamp(mCommits.get(0).getCommitBody()
                                .getCommitter().getDate());
            } catch (ParseException e) {
                Crashlytics.log("Date Parser error");
                Crashlytics.logException(e);
            }

            if (newLastDate > lastDate) {
                Commit updatedCommit = mCommits.get(0);
                String commitRepo = updatedCommit.getHtmlUrl().split("/")[4];
                String commitOwner = updatedCommit.getHtmlUrl().split("/")[3];

                Intent intent = LastCommitDetailsActivity.newIntent(mContext,
                        new String[] {commitOwner, commitRepo});

                PendingIntent pi = PendingIntent.getActivity(mContext,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notif = new NotificationCompat.Builder(mContext,
                        NotificationChannel.DEFAULT_CHANNEL_ID)
                        .setTicker(getResources().getString(R.string.new_commit_headline))
                        .setContentTitle(getResources().getString(R.string.new_commit_headline))
                        .setContentText(commitRepo)
                        .setContentIntent(pi)
                        .setSmallIcon(R.drawable.ic_info_24dp)
                        .setAutoCancel(true)
                        .build();

                showBackgroundNotif(0, notif);

                mQueryPrefs.setLastResultDate(newLastDate);
            }
        }

        private void showBackgroundNotif(int requestCode, Notification notif) {
            Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
            i.putExtra(REQUEST_CODE, requestCode);
            i.putExtra(NOTIFICATION, notif);
            sendOrderedBroadcast(i, PERMISSION_PRIVATE, null, null,
                    Activity.RESULT_OK, null, null);
        }
    }
}
