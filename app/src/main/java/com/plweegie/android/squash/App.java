package com.plweegie.android.squash;


import android.app.Application;

import com.plweegie.android.squash.modules.AppModule;
import com.plweegie.android.squash.modules.DaggerNetComponent;
import com.plweegie.android.squash.modules.NetComponent;
import com.plweegie.android.squash.modules.NetModule;

public class App extends Application {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private NetComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(GITHUB_BASE_URL))
                .build();
    }

    public NetComponent getNetComponent() {
        return mComponent;
    }
}
