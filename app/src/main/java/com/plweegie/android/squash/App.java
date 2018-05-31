package com.plweegie.android.squash;


import android.app.Application;

import com.plweegie.android.squash.data.RoomModule;
import com.plweegie.android.squash.rest.DaggerNetComponent;
import com.plweegie.android.squash.rest.NetComponent;
import com.plweegie.android.squash.rest.NetModule;
import com.plweegie.android.squash.rest.SharedPrefModule;
import com.plweegie.android.squash.services.WorkerModule;
import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static final String DATABASE_NAME = "repos";

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(GITHUB_BASE_URL))
                .sharedPrefModule(new SharedPrefModule())
                .roomModule(new RoomModule(DATABASE_NAME))
                .workerModule(new WorkerModule())
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}
