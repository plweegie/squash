/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.os.Bundle;
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

public class CommitListFragment extends Fragment {

    public static final String GITHUB_BASE_URL = "https://api.github.com/";

    private List<Repository> mRepos;
    private RecyclerView mRecyclerView;
    private RepoAdapter mAdapter;

    private Retrofit mRetrofit;
    private GitHubService mService;

    public static CommitListFragment newInstance() {
        return new CommitListFragment();
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
        
        return v;
    }
}
