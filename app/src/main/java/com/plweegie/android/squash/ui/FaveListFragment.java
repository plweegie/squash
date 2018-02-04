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

import android.arch.lifecycle.ViewModelProviders;
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
import com.plweegie.android.squash.App;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.adapters.FaveAdapter;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.data.RepoRepository;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.QueryPreferences;
import com.plweegie.android.squash.utils.SchedulerUtil;
import com.plweegie.android.squash.viewmodels.FaveListViewModel;
import com.plweegie.android.squash.viewmodels.FaveListViewModelFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaveListFragment extends Fragment implements FaveAdapter.FaveAdapterOnClickHandler {

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

        SchedulerUtil.scheduleCommitPoll(getActivity());
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

            Observable.fromIterable(repoEntries)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(repo -> mService.getCommits(repo.getOwner().getLogin(),
                            repo.getName(), 1, mAuthToken))
                    .subscribe(new Observer<List<Commit>>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(List<Commit> commits) {
                            repoLastCommits.add(commits.get(0));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Crashlytics.log("Retrofit error");
                            Crashlytics.logException(e);
                        }

                        @Override
                        public void onComplete() {}
                    });

            if (!repoLastCommits.isEmpty()) {
                Collections.sort(repoLastCommits, new QueryPreferences.CommitCreatedComparator());
                long lastDate = mQueryPrefs.getLastResultDate();
                long newLastDate = 0L;
                try {
                    newLastDate = DateUtils.convertToTimestamp(repoLastCommits.get(0).getCommitBody()
                                    .getCommitter().getDate());
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
