package com.plweegie.android.squash;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = AndroidInjectionModule.class)
public interface AppComponent  {
    void inject(App app);
}
