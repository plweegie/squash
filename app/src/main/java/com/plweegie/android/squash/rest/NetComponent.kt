package com.plweegie.android.squash.rest


import com.plweegie.android.squash.AppModule
import com.plweegie.android.squash.data.RoomModule
import com.plweegie.android.squash.services.CommitPollWorker
import com.plweegie.android.squash.ui.FaveListFragment
import com.plweegie.android.squash.ui.LastCommitDetailsActivity
import com.plweegie.android.squash.ui.RepoListFragment
import com.plweegie.android.squash.ui.RepoReadmeActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetModule::class, SharedPrefModule::class, RoomModule::class])
interface NetComponent {
    fun inject(lastCommitDetailsActivity: LastCommitDetailsActivity)
    fun inject(activity: RepoReadmeActivity)
    fun inject(fragment: RepoListFragment)
    fun inject(fragment: FaveListFragment)
    fun inject(worker: CommitPollWorker)
}
