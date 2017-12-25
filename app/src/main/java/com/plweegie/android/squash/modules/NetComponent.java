package com.plweegie.android.squash.modules;


import com.plweegie.android.squash.FaveListFragment;
import com.plweegie.android.squash.LastCommitDetailsActivity;
import com.plweegie.android.squash.RepoListFragment;
import com.plweegie.android.squash.services.CommitPollService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(LastCommitDetailsActivity lastCommitDetailsActivity);
    void inject(RepoListFragment fragment);
    void inject(FaveListFragment fragment);
    void inject(CommitPollService service);
}
