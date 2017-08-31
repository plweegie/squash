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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plweegie.android.squash.adapters.FaveAdapter;
import com.plweegie.android.squash.utils.Repository;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class FaveListFragment extends Fragment {
    
    private List<Repository> mFaveRepos;
    private RecyclerView mRecyclerView;
    private FaveAdapter mAdapter;
    
    private DatabaseReference mDatabase;
    
    public static FaveListFragment newInstance() {
        return new FaveListFragment();
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
        
        mFaveRepos = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("repositories");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.commits_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));
        
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    mFaveRepos.add(child.getValue(Repository.class));
                }
                if (mAdapter == null) {
                    mAdapter = new FaveAdapter(getActivity(), mFaveRepos);
                    mAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setContent(mFaveRepos);
                    mAdapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FaveListFragment", error.toException().toString());
            }
        });
        return v;
    }
}
