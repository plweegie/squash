package com.plweegie.android.squash;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

import com.plweegie.android.squash.services.CommitPollService;

public abstract class VisibleActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(CommitPollService.ACTION_SHOW_NOTIFICATION);
        this.registerReceiver(mOnShowNotification, filter,
                CommitPollService.PERMISSION_PRIVATE, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mOnShowNotification);
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
