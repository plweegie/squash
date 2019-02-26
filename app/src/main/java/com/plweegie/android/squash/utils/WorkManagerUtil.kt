package com.plweegie.android.squash.utils

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.plweegie.android.squash.services.CommitPollWorker
import java.util.concurrent.TimeUnit


class WorkManagerUtil {

    companion object {

        private const val GITHUB_CHECK_TAG = "github_check"

        @JvmStatic
        fun enqueueWorkRequest() {
            val constraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val request = PeriodicWorkRequestBuilder<CommitPollWorker>(15, TimeUnit.MINUTES)
                    .addTag(GITHUB_CHECK_TAG)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance().enqueue(request)
        }
    }
}