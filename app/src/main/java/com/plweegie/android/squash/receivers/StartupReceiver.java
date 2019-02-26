package com.plweegie.android.squash.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.plweegie.android.squash.utils.WorkManagerUtil;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            WorkManagerUtil.enqueueWorkRequest();
        }
    }
}
