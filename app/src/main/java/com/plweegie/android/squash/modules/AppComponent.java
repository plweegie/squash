package com.plweegie.android.squash.modules;

import com.plweegie.android.squash.App;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = AndroidInjectionModule.class)
public interface AppComponent  {
    void inject(App app);
}
