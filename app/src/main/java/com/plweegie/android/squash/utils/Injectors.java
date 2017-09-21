package com.plweegie.android.squash.utils;

import android.content.Context;

import com.plweegie.android.squash.data.RepoDatabase;
import com.plweegie.android.squash.data.RepoRepository;

/**
 * Created by jan on 19/09/17.
 */

public class Injectors {
    public static RepoRepository provideRepository(Context context) {
        RepoDatabase db = RepoDatabase.getInstance(context);
        AppExecutors executors = AppExecutors.getInstance();
        return RepoRepository.getInstance(db.repoDao(), executors);
    }
}
