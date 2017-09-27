/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.RepoEntry;

import java.util.ArrayList;
import java.util.List;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoHolder> {
    
    private List<RepoEntry> mRepos;
    private Context mContext;
    private boolean isLoadingAdded = false;

    private final RepoAdapterOnClickHandler mClickHandler;
    
    public RepoAdapter(Context context,
                       RepoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mRepos = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public List<RepoEntry> getRepos() {
        return mRepos;
    }

    public void setRepos(List<RepoEntry> repos) {
        mRepos = repos;
    }

    @Override
    public RepoHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new RepoHolder(inflater, vg, R.layout.repo_view_holder);
    }

    @Override
    public void onBindViewHolder(RepoHolder vh, int i) {
        RepoEntry repo = mRepos.get(i);
        vh.bind(repo);
    }

    @Override
    public int getItemCount() {
        return mRepos == null ? 0 : mRepos.size();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(RepoEntry repo) {
        mRepos.add(repo);
        notifyItemInserted(mRepos.size() - 1);
    }

    public void addAll(List<RepoEntry> repos) {
        for (RepoEntry r : repos) {
            add(r);
        }
    }

    public void remove(RepoEntry repo) {
        int position = mRepos.indexOf(repo);
        if (position > -1) {
            mRepos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public RepoEntry getItem(int position) {
        return mRepos.get(position);
    }

    public interface RepoAdapterOnClickHandler {
        void onItemClick(int position);
    }
    
    public class RepoHolder extends RecyclerView.ViewHolder {
        
        private TextView mNameTextView;
        private TextView mLangTextView;
        private TextView mStarCountTextView;
        private TextView mWatchCountTextView;
        private ImageView mFavoriteImgView;

        
        public RepoHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));

            mNameTextView = itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = itemView.findViewById(R.id.watchers_tv);
            mFavoriteImgView = itemView.findViewById(R.id.fave_image_view);
        }
        
        public void bind(RepoEntry repo) {
            
            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(Integer.toString(repo.getStargazersCount()));
            mWatchCountTextView.setText(Integer.toString(repo.getWatchersCount()));
            
            mFavoriteImgView.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            mFavoriteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, repo.getName() + " added to Favorites",
                            Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    mClickHandler.onItemClick(position);
                }
            });
        }
    }
}
