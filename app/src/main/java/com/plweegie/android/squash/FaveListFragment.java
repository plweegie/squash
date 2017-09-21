/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.arch.lifecycle.ViewModelProviders;
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
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.utils.Injectors;
import com.plweegie.android.squash.viewmodels.FaveListViewModel;
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory;

import java.util.List;

/**
 *
 * @author jan
 */
public class FaveListFragment extends Fragment {
    
    private RecyclerView mRecyclerView;
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

        FaveListViewModelFactory factory = new FaveListViewModelFactory(
                Injectors.provideRepository(getActivity()));
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(FaveListViewModel.class);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.commit_list_fragment, parent, false);
        
        mRecyclerView = v.findViewById(R.id.commits_recycler_view);
        mIndicator = v.findViewById(R.id.load_indicator);
        mIndicator.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        mAdapter = new FaveAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getFaveList().observe(this, repoEntries -> {
            mAdapter.setContent(repoEntries);
        });
        
        return v;
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
                //TODO Clear db
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
