package com.plweegie.android.squash.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.plweegie.android.squash.services.CommitPollService;

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
                    .setPeriodic(60 * 60 * 1000)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        }
    }
}
