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
import com.plweegie.android.squash.utils.Injectors;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jan on 10/10/17.
 */

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

            String lastSha = QueryPreferences.getLastResultSha(mContext);

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

            String newLastSha = commits.get(0).getSha();
            if(!newLastSha.equals(lastSha)) {
                Commit updatedCommit = commits.get(0);
                String commitRepo = updatedCommit.getHtmlUrl().split("/")[4];
                String commitOwner = updatedCommit.getHtmlUrl().split("/")[3];

                Intent intent = LastCommitDetailsActivity.newIntent(mContext,
                        new String[] {commitOwner, commitRepo});

                PendingIntent pi = PendingIntent.getActivity(mContext,
                        0, intent, 0);

                Notification notif = new NotificationCompat.Builder(mContext,
                        NotificationChannel.DEFAULT_CHANNEL_ID)
                        .setTicker(getResources().getString(R.string.new_commit_headline))
                        .setContentTitle(getResources().getString(R.string.new_commit_headline))
                        .setContentText(commitRepo)
                        .setContentIntent(pi)
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notifManager = NotificationManagerCompat
                        .from(CommitPollService.this);
                notifManager.notify(0, notif);

                QueryPreferences.setLastResultSha(mContext, newLastSha);
            }
            jobFinished(jobParams, false);
            return null;
        }
    }
}
