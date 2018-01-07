/*
 * Copyright (c) 2017 Jan K Szymanski

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.plweegie.android.squash;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.plweegie.android.squash.adapters.FaveAdapter;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.services.CommitPollService;
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.QueryPreferences;
import com.plweegie.android.squash.viewmodels.FaveListViewModel;
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaveListFragment extends Fragment implements FaveAdapter.FaveAdapterOnClickHandler {

    private static final int POLL_JOB_ID = 112;

    @Inject
    GitHubService mService;
    @Inject
    QueryPreferences mQueryPrefs;
    @Inject
    RepoRepository mDataRepository;
    
    private RecyclerView mRecyclerView;
    private FaveAdapter mAdapter;
    private ProgressBar mIndicator;
    private FaveListViewModel mViewModel;
    private String mAuthToken;
    
    public static FaveListFragment newInstance() {
        return new FaveListFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FaveListViewModelFactory factory = new FaveListViewModelFactory(mDataRepository);
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(FaveListViewModel.class);

        mAuthToken = mQueryPrefs.getStoredAccessToken();

        JobScheduler scheduler = (JobScheduler) getActivity()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;
        for (JobInfo info: scheduler.getAllPendingJobs()) {
            if(info.getId() == POLL_JOB_ID) {
                hasBeenScheduled = true;
            }
        }

        if (!hasBeenScheduled) {
            JobInfo jobInfo = new JobInfo.Builder(POLL_JOB_ID,
                    new ComponentName(getActivity(), CommitPollService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(60 * 60 * 1000)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        }
    }

    @Override
    public void onAttach(Context context) {
        ((App) getActivity().getApplication()).getNetComponent().inject(this);
        super.onAttach(context);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, parent, false);
        List<Commit> repoLastCommits = new ArrayList<>();
        
        mRecyclerView = v.findViewById(R.id.commits_recycler_view);
        mIndicator = v.findViewById(R.id.load_indicator);
        mIndicator.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        mAdapter = new FaveAdapter(getActivity(), FaveListFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getFaveList().observe(this, repoEntries -> {
            mAdapter.setContent(repoEntries);
            for(int i = 0; i < mAdapter.getItemCount(); i++) {
                RepoEntry repo = mAdapter.getItem(i);

                Call<List<Commit>> call = mService.getCommits(repo.getOwner().getLogin(),
                        repo.getName(), 1, mAuthToken);

                call.enqueue(new Callback<List<Commit>>() {
                    @Override
                    public void onResponse(Call<List<Commit>> call, Response<List<Commit>> response) {
                        Commit commit = response.body().get(0);
                        repoLastCommits.add(commit);
                    }

                    @Override
                    public void onFailure(Call<List<Commit>> call, Throwable t) {
                        Crashlytics.log("Retrofit error");
                        Crashlytics.logException(t);
                    }
                });
            }

            if (!repoLastCommits.isEmpty()) {
                Collections.sort(repoLastCommits, new QueryPreferences.CommitCreatedComparator());
                long lastDate = mQueryPrefs.getLastResultDate();
                long newLastDate = 0L;
                try {
                    newLastDate = DateUtils.convertToTimestamp(repoLastCommits.get(0).getCommitBody()
                                    .getCommitBodyAuthor().getDate());
                } catch(ParseException e) {
                    Crashlytics.log("Date parser error");
                    Crashlytics.logException(e);
                }

                if (newLastDate > lastDate) {
                    mQueryPrefs.setLastResultDate(newLastDate);
                }
            }
        });

        return v;
    }

    @Override
    public void onItemClick(long repoId) {
        mDataRepository.deleteRepo(repoId);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fave_list_menu, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clear_db:
                mDataRepository.deleteAllRepos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
