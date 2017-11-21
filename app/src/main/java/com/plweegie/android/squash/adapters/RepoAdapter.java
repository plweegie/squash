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
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.SettingsFragment;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoHolder> {
    
    private List<RepoEntry> mRepos;
    private Context mContext;
    private Comparator<RepoEntry> mComparator;

    private final RepoAdapterOnClickHandler mClickHandler;
    
    public RepoAdapter(Context context,
                       RepoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mRepos = new ArrayList<>();
        mClickHandler = clickHandler;
        mComparator = new QueryPreferences.RepoNameComparator();
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

    public void sort() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int sortedBy = Integer.parseInt(prefs.getString(SettingsFragment
                .KEY_PREF_SORT_BY_SETTING, "-1"));
        switch(sortedBy) {
            case 1:
                mComparator = new QueryPreferences.RepoCreatedComparator();
                break;
            case 2:
                mComparator = new QueryPreferences.RepoStarsComparator();
                break;
            default:
                mComparator = new QueryPreferences.RepoNameComparator();
        }
        Collections.sort(mRepos, mComparator);
        notifyDataSetChanged();
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
        private ImageView mInvisibleImgView;

        public RepoHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));

            mNameTextView = itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = itemView.findViewById(R.id.watchers_tv);
            mFavoriteImgView = itemView.findViewById(R.id.fave_image_view);
            mInvisibleImgView = itemView.findViewById(R.id.last_commit_image_view);
        }
        
        public void bind(RepoEntry repo) {
            
            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(Integer.toString(repo.getStargazersCount()));
            mWatchCountTextView.setText(Integer.toString(repo.getWatchersCount()));

            mInvisibleImgView.setVisibility(View.GONE);

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
