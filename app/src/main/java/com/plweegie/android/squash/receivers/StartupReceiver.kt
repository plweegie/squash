package com.plweegie.android.squash.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import com.crashlytics.android.Crashlytics
import com.plweegie.android.squash.utils.WorkManagerUtil


class StartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            Crashlytics.log("Boot action received")
            WorkManagerUtil.enqueueWorkRequest(context,
                    policy = ExistingPeriodicWorkPolicy.REPLACE)
        }
    }
}