/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.utils.Repository;
import java.util.List;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoHolder> {
    
    private List<Repository> mRepos;
    private Context mContext;
    
    public RepoAdapter(Context context, List<Repository> repos) {
        mRepos = repos;
        mContext = context;
    }

    @Override
    public RepoHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new RepoHolder(inflater, vg, R.layout.repo_view_holder);
    }

    @Override
    public void onBindViewHolder(RepoHolder vh, int i) {
        Repository repo = mRepos.get(i);
        vh.bind(repo);
    }

    @Override
    public int getItemCount() {
        return mRepos.size();
    }
    
    public void setContent(List<Repository> repos) {
        mRepos = repos;
    }
    
    public class RepoHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        
        private Repository mRepo;
        private DatabaseReference mDatabase;
        
        private TextView mNameTextView;
        private TextView mLangTextView;
        private TextView mStarCountTextView;
        private TextView mWatchCountTextView;
        
        public RepoHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));
            itemView.setOnClickListener(this);
            mDatabase = FirebaseDatabase.getInstance().getReference("repositories");
            
            mNameTextView = (TextView) itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = (TextView) itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = (TextView) itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = (TextView) itemView.findViewById(R.id.watchers_tv);
        }
        
        public void bind(Repository repo) {
            
            mRepo = repo;
            
            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(repo.getStargazersCount().toString());
            mWatchCountTextView.setText(repo.getWatchersCount().toString());
        }
        
        @Override
        public void onClick(View view) {
            
            int position = getAdapterPosition();
            mDatabase.child(String.valueOf(position))
                    .setValue(mRepo);
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putBoolean(String.valueOf(position), true)
                    .apply();
        }
        
//        private void toggleFavorite() {
//            mRepo.setIsFavorite(!mRepo.isFavorite());
//        }
    }
}
