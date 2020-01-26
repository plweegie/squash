package com.plweegie.android.squash.utils

import android.content.Context
import androidx.work.*
import com.plweegie.android.squash.services.CommitPollWorker
import java.util.concurrent.TimeUnit


class WorkManagerUtil {

    companion object {

        private const val GITHUB_CHECK_TAG = "github_check"

        @JvmStatic
        @JvmOverloads
        fun enqueueWorkRequest(context: Context,
                               policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val request = PeriodicWorkRequestBuilder<CommitPollWorker>(15, TimeUnit.MINUTES)
                    .addTag(GITHUB_CHECK_TAG)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    GITHUB_CHECK_TAG,
                    policy,
                    request)
        }
    }
}