package com.plweegie.android.squash.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.RepoEntry;

import java.util.ArrayList;
import java.util.List;

public class BaseGithubAdapter extends
        RecyclerView.Adapter<BaseGithubAdapter.BaseViewHolder> {

    protected List<RepoEntry> mRepos;
    protected Context mContext;

    public BaseGithubAdapter(Context context) {
        mContext = context;
        mRepos = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new BaseViewHolder(inflater, vg, R.layout.repo_view_holder);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder vh, int i) {
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

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        protected TextView mNameTextView;
        protected TextView mLangTextView;
        protected TextView mStarCountTextView;
        protected TextView mWatchCountTextView;

        public BaseViewHolder(LayoutInflater inflater, ViewGroup parent,
                          int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));

            mNameTextView = itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = itemView.findViewById(R.id.watchers_tv);
        }

        public void bind(RepoEntry repo) {

            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(Integer.toString(repo.getStargazersCount()));
            mWatchCountTextView.setText(Integer.toString(repo.getWatchersCount()));
        }
    }
}
