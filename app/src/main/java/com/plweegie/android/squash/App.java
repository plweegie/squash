package com.plweegie.android.squash;


import android.app.Application;

import com.plweegie.android.squash.modules.AppModule;
import com.plweegie.android.squash.modules.DaggerNetComponent;
import com.plweegie.android.squash.modules.NetComponent;
import com.plweegie.android.squash.modules.NetModule;
import com.plweegie.android.squash.modules.SharedPrefModule;

public class App extends Application {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(GITHUB_BASE_URL))
                .sharedPrefModule(new SharedPrefModule())
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}
