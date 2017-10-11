/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

import com.plweegie.android.squash.adapters.FaveAdapter;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.services.CommitPollService;
import com.plweegie.android.squash.utils.Injectors;
import com.plweegie.android.squash.viewmodels.FaveListViewModel;
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory;

/**
 *
 * @author jan
 */
public class FaveListFragment extends Fragment implements FaveAdapter.FaveAdapterOnClickHandler {

    private static final int POLL_JOB_ID = 112;
    
    private RecyclerView mRecyclerView;
    private RepoRepository mDataRepository;
    private FaveAdapter mAdapter;
    private ProgressBar mIndicator;
    private FaveListViewModel mViewModel;
    
    public static FaveListFragment newInstance() {
        return new FaveListFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDataRepository = Injectors.provideRepository(getActivity());
        FaveListViewModelFactory factory = new FaveListViewModelFactory(mDataRepository);
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(FaveListViewModel.class);

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
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(60 * 60 * 1000)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, parent, false);
        
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
        });

        /*for(RepoEntry repo: repoEntries) {
        }*/
        
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
