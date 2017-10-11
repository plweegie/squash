package com.plweegie.android.squash.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import com.plweegie.android.squash.utils.QueryPreferences;

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
        mTask = new CommitPollTask();
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

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];

            String lastSha = QueryPreferences.getLastResultSha(getApplicationContext());

            jobFinished(jobParams, false);
            return null;
        }
    }
}
