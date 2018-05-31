package com.plweegie.android.squash.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.plweegie.android.squash.services.CommitPollService;
import com.plweegie.android.squash.services.CommitPollWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class SchedulerUtil {

    private static final int POLL_JOB_ID = 112;

    public static void scheduleCommitPoll(Context context) {
        JobScheduler scheduler = (JobScheduler) context
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;
        for (JobInfo info: scheduler.getAllPendingJobs()) {
            if(info.getId() == POLL_JOB_ID) {
                hasBeenScheduled = true;
            }
        }

        if (!hasBeenScheduled) {
            JobInfo jobInfo = new JobInfo.Builder(POLL_JOB_ID,
                    new ComponentName(context, CommitPollService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(30 * 60 * 1000)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        }
    }

    public static void enqueueWorkRequest() {

        Constraints workConstraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                CommitPollWorker.class,
                15L,
                TimeUnit.MINUTES)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance().enqueue(request);
    }
}
