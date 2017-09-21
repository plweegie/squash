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
import android.widget.ProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plweegie.android.squash.adapters.CommitAdapter;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.utils.GitHubService;
import com.plweegie.android.squash.utils.Commit;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class
CommitListFragment extends Fragment {
    
    public static final String GITHUB_BASE_URL = "https://api.github.com/";
    public static final int MAXIMUM_LIST_LENGTH = 5;
    
    private List<Commit> mCommits;
    private List<RepoEntry> mFaveRepos;
    
    private RecyclerView mRecyclerView;
    private ProgressBar mIndicator;
    private CommitAdapter mAdapter;
    
    private DatabaseReference mDatabase;
    private ValueEventListener mDbListener;
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
        
        mRecyclerView = v.findViewById(R.id.commits_recycler_view);
        mIndicator = v.findViewById(R.id.load_indicator);
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
        
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        mCommits = new ArrayList<>();
        mFaveRepos = new ArrayList<>();
        
        mDatabase = FirebaseDatabase.getInstance().getReference("repositories");
        mDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!mFaveRepos.isEmpty()) {
                    mFaveRepos.clear();
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    mFaveRepos.add(child.getValue(RepoEntry.class));
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CommitListFragment", error.toException().toString());
            }
        };
        
        mDatabase.addValueEventListener(mDbListener);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(mDbListener);
    }
    
    public void updateUI() {
        
        for (RepoEntry repo: mFaveRepos) {
            String repoName = repo.getName();
            String repoOwner = repo.getOwner().getLogin();
            
            Call<List<Commit>> call = mService.getCommits(repoOwner, repoName,
                    MAXIMUM_LIST_LENGTH);
            
            call.enqueue(new Callback<List<Commit>>() {
                @Override
                public void onResponse(Call<List<Commit>> call,
                        Response<List<Commit>> response) {
                    if (!mCommits.isEmpty()) {
                        mCommits.clear();
                    }
                    mCommits.addAll(response.body());

                    if (mAdapter == null) {
                        mAdapter = new CommitAdapter(getActivity(), mCommits);
                        mAdapter.setHasStableIds(true);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.setContent(mCommits);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Commit>> call, Throwable t) {
                    Log.e("CommitListFragment", "Retrofit error: " + t);
                }
            });
        }
    }
}
