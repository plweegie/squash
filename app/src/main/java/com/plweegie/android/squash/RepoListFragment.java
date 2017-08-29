/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.plweegie.android.squash.adapters.RepoAdapter;
import com.plweegie.android.squash.utils.GitHubService;
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
    
    private List<Repository> mRepos;
    private RecyclerView mRecyclerView;
    private RepoAdapter mAdapter;
    
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
        
        mRepos = new ArrayList<>();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.commits_recycler_view);
        
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
    
    public void updateUI() {
        Call<List<Repository>> call = mService.getRepos("fuchsia-mirror", 10);

        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call,
                    Response<List<Repository>> response) {
                mRepos.addAll(response.body());
                
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                for (int i = 0; i < mRepos.size(); i++) {
                    if (prefs.contains(String.valueOf(i))) {
                        mRepos.get(i).setIsFavorite(true);
                    }
                }
                
                if (mAdapter == null) {
                    mAdapter = new RepoAdapter(getActivity(), mRepos);
                    mAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setContent(mRepos);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Log.e("RepoListFragment", "Retrofit error: " + t);
            }
        });
    }
}
