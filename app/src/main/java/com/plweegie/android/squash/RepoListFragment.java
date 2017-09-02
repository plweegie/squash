/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.plweegie.android.squash.adapters.RepoAdapter;
import com.plweegie.android.squash.utils.GitHubService;
import com.plweegie.android.squash.utils.QueryPreferences;
import com.plweegie.android.squash.utils.Repository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RepoListFragment extends Fragment {
    
    public static final String GITHUB_BASE_URL = "https://api.github.com/";
    public static final int MAXIMUM_LIST_LENGTH = 50;
    
    private List<Repository> mRepos;
    private RecyclerView mRecyclerView;
    private RepoAdapter mAdapter;
    private ProgressBar mIndicator;
    private InputMethodManager mImm;
    
    private Retrofit mRetrofit;
    private GitHubService mService;
    
    public static RepoListFragment newInstance() {
        return new RepoListFragment();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.commit_list_fragment, parent, false);
        
        mImm = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mRepos = new ArrayList<>();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.commits_recycler_view);
        mIndicator = (ProgressBar) v.findViewById(R.id.load_indicator);
        mIndicator.setVisibility(View.GONE);
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));
        
        mRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = mRetrofit.create(GitHubService.class);
        updateUI();
        
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.repo_list_menu, menu);
        
        final MenuItem searchItem = menu.findItem(R.id.repo_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String string) {
                mImm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                
                mRecyclerView.setVisibility(View.INVISIBLE);
                mIndicator.setVisibility(View.VISIBLE);
                
                QueryPreferences.setStoredQuery(getActivity(), string);
                updateUI();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String string) {
                return true;
            }
        });
        
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }
    
    public void updateUI() {
        
        final String apiQuery = QueryPreferences.getStoredQuery(getActivity());
        Call<List<Repository>> call = mService.getRepos(apiQuery, MAXIMUM_LIST_LENGTH);

        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call,
                    Response<List<Repository>> response) {
                
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "No repositories found for " +
                            apiQuery, Toast.LENGTH_SHORT).show();
                    mIndicator.setVisibility(View.GONE);
                    return;
                }
                
                if (!mRepos.isEmpty()) {
                    mRepos.clear();
                }
                mRepos.addAll(response.body());
                
                if (mAdapter == null) {
                    mAdapter = new RepoAdapter(getActivity(), mRepos,
                            RepoAdapter.REPOS_LIST_MODE);
                    mAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setContent(mRepos);
                    mAdapter.notifyDataSetChanged();
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                mIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Log.e("RepoListFragment", "Retrofit error: " + t);
            }
        });
    }
}
