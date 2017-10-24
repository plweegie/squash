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
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plweegie.android.squash.LastCommitDetailsActivity;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.RepoEntry;

import java.util.List;


public class FaveAdapter extends RecyclerView.Adapter<FaveAdapter.FaveHolder> {

    private List<RepoEntry> mRepos;
    private Context mContext;
    private FaveAdapterOnClickHandler mClickHandler;

    public FaveAdapter(Context context, FaveAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public FaveHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new FaveHolder(inflater, vg, R.layout.repo_view_holder);
    }

    @Override
    public void onBindViewHolder(FaveHolder vh, int i) {
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

    public RepoEntry getItem(int position) {
        return mRepos.get(position);
    }

    public void setContent(List<RepoEntry> repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }

    public interface FaveAdapterOnClickHandler {
        void onItemClick(long repoId);
    }

    public class FaveHolder extends RecyclerView.ViewHolder {

        protected TextView mNameTextView;
        protected TextView mLangTextView;
        protected TextView mStarCountTextView;
        protected TextView mWatchCountTextView;
        protected ImageView mFavoriteImgView;

        public FaveHolder(LayoutInflater inflater, ViewGroup parent,
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = LastCommitDetailsActivity.newIntent(mContext,
                            new String[] {repo.getOwner().getLogin(), repo.getName()});
                    mContext.startActivity(intent);
                    return true;
                }
            });

            mFavoriteImgView.setImageResource(R.drawable.ic_delete_black_24dp);
            mFavoriteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mClickHandler.onItemClick(mRepos.get(position).getRepoId());
                }
            });
        }
    }
}

