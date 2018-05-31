package com.plweegie.android.squash.services;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import dagger.Module;
import dagger.Provides;

@Module
public class WorkerModule {

    @Provides
    @Singleton
    Constraints provideConstraints() {
        return new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
    }

    @Provides
    @Singleton
    PeriodicWorkRequest provideWorkRequest(Constraints constraints) {
        return new PeriodicWorkRequest.Builder(
                CommitPollWorker.class,
                15L,
                TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
    }
}