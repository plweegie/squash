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
package com.plweegie.android.squash.ui;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.plweegie.android.squash.App;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.adapters.FaveAdapter;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.viewmodels.FaveListViewModel;
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory;

import javax.inject.Inject;

public class FaveListFragment extends Fragment implements FaveAdapter.FaveAdapterOnClickHandler {

    @Inject
    RepoRepository mDataRepository;
    
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

        FaveListViewModelFactory factory = new FaveListViewModelFactory(mDataRepository);
        mViewModel = ViewModelProviders.of(getActivity(), factory).get(FaveListViewModel.class);
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
        
        mRecyclerView = v.findViewById(R.id.commits_recycler_view);
        mIndicator = v.findViewById(R.id.load_indicator);
        mIndicator.setVisibility(View.GONE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        mAdapter = new FaveAdapter(getActivity(), FaveListFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getFaveList().observe(this, repoEntries ->
            mAdapter.setContent(repoEntries));

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
