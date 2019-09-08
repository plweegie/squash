package com.plweegie.android.squash


import android.app.Application

import com.plweegie.android.squash.data.RoomModule
import com.plweegie.android.squash.rest.DaggerNetComponent
import com.plweegie.android.squash.rest.NetComponent
import com.plweegie.android.squash.rest.NetModule
import com.plweegie.android.squash.rest.SharedPrefModule
import com.squareup.leakcanary.LeakCanary

class App : Application() {

    val netComponent: NetComponent by lazy {
        DaggerNetComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule(GITHUB_BASE_URL))
                .sharedPrefModule(SharedPrefModule())
                .roomModule(RoomModule(DATABASE_NAME))
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    companion object {
        private const val GITHUB_BASE_URL = "https://api.github.com/"
        private const val DATABASE_NAME = "repos"
    }
}
