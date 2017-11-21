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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.plweegie.android.squash.LastCommitDetailsActivity;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.rest.RestClient;
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.Injectors;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class CommitPollService extends JobService {
    private static final String TAG = "CommitPollService";

    private CommitPollTask mTask;

    public CommitPollService() {
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
        return true;
    }

    private class CommitPollTask extends AsyncTask<JobParameters, Void, Void> {

        private Context mContext;

        public CommitPollTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];
            RestClient client = new RestClient(mContext);
            GitHubService service = client.getApiService();

            RepoRepository repository = Injectors.provideRepository(mContext);
            List<RepoEntry> repos = repository.getAllFavoritesDirectly();

            List<Commit> commits = new ArrayList<>();

            long lastDate = QueryPreferences.getLastResultDate(mContext);
            long newLastDate = 0L;

            for (RepoEntry entry: repos) {
                Call<List<Commit>> call = service.getCommits(entry.getOwner().getLogin(),
                        entry.getName(), 1);
                try {
                    Commit commit = call.execute().body().get(0);
                    commits.add(commit);
                } catch(IOException e) {
                    Log.e(TAG, "Error checking for new commits");
                    jobFinished(jobParams, true);
                }
            }

            Collections.sort(commits, new QueryPreferences.CommitCreatedComparator());

            try {
                newLastDate = commits.isEmpty() ? lastDate :
                        DateUtils.convertToTimestamp(commits.get(0).getCommitBody()
                                .getCommitBodyAuthor().getDate());
            } catch (ParseException e) {
                Log.e("CommitPollService", "Date parser error: " + e);
            }

            if(newLastDate > lastDate) {
                Commit updatedCommit = commits.get(0);
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
                        //.setContentTitle(String.valueOf(lastDate))
                        .setContentText(commitRepo)
                        //.setContentText(String.valueOf(newLastDate))
                        .setContentIntent(pi)
                        .setSmallIcon(R.drawable.ic_info_24dp)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notifManager = NotificationManagerCompat
                        .from(CommitPollService.this);
                notifManager.notify(0, notif);

                QueryPreferences.setLastResultDate(mContext, newLastDate);
            }
            jobFinished(jobParams, false);
            return null;
        }
    }
}
