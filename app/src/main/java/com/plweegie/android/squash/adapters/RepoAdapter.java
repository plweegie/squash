/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.utils.Repository;
import java.util.List;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoHolder> {
    
    public static final int REPOS_LIST_MODE = 0;
    public static final int FAVES_LIST_MODE = 1;
    
    private List<Repository> mRepos;
    private Context mContext;
    private int mDisplayMode;
    
    public RepoAdapter(Context context, List<Repository> repos, int mode) {
        mRepos = repos;
        mContext = context;
        mDisplayMode = mode;
    }

    @Override
    public RepoHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        
        switch(mDisplayMode) {
            case REPOS_LIST_MODE:
                return new RepoHolder(inflater, vg, R.layout.repo_view_holder);
            case FAVES_LIST_MODE:
                return new FaveHolder(inflater, vg, R.layout.repo_view_holder);
            default:
                throw new IllegalArgumentException("Could not initialize view holders");
        }
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
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setContent(List<Repository> repos) {
        mRepos = repos;
    }
    
    public class RepoHolder extends RecyclerView.ViewHolder {
        
        protected Repository mRepo;
        protected DatabaseReference mDatabase;
        
        protected TextView mNameTextView;
        protected TextView mLangTextView;
        protected TextView mStarCountTextView;
        protected TextView mWatchCountTextView;
        protected ImageView mFavoriteImgView;
        
        public RepoHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));

            mDatabase = FirebaseDatabase.getInstance().getReference("repositories");
            
            mNameTextView = (TextView) itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = (TextView) itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = (TextView) itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = (TextView) itemView.findViewById(R.id.watchers_tv);
            mFavoriteImgView = (ImageView) itemView.findViewById(R.id.fave_image_view);
        }
        
        public void bind(Repository repo) {
            
            mRepo = repo;
            
            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(repo.getStargazersCount().toString());
            mWatchCountTextView.setText(repo.getWatchersCount().toString());
            
            mFavoriteImgView.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            mFavoriteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.child(String.valueOf(mRepo.getId()))
                            .setValue(mRepo);
                    Toast.makeText(mContext, mRepo.getName() + " added to Favorites",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    public class FaveHolder extends RepoHolder {
        
        public FaveHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater, parent, layoutResId);
        }
        
        @Override
        public void bind(Repository repo) {
            mRepo = repo;

            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(repo.getStargazersCount().toString());
            mWatchCountTextView.setText(repo.getWatchersCount().toString());

            mFavoriteImgView.setImageResource(R.drawable.ic_delete_black_24dp);
            mFavoriteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mDatabase.child(String.valueOf(mRepo.getId()))
                            .removeValue();
                    mRepos.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
