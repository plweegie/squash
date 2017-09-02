/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.utils.Repository;
import java.util.List;

public class FaveAdapter extends RecyclerView.Adapter<FaveAdapter.FaveHolder> {

    private List<Repository> mFaveRepos;
    private Context mContext;

    public FaveAdapter(Context context, List<Repository> repos) {
        mFaveRepos = repos;
        mContext = context;
    }

    @Override
    public FaveHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new FaveHolder(inflater, vg, R.layout.repo_view_holder);
    }

    @Override
    public void onBindViewHolder(FaveHolder vh, int i) {
        Repository repo = mFaveRepos.get(i);
        vh.bind(repo);
    }

    @Override
    public int getItemCount() {
        return mFaveRepos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setContent(List<Repository> repos) {
        mFaveRepos = repos;
    }

    public class FaveHolder extends RecyclerView.ViewHolder {

        private Repository mRepo;
        private DatabaseReference mDatabase;

        private TextView mNameTextView;
        private TextView mLangTextView;
        private TextView mStarCountTextView;
        private TextView mWatchCountTextView;
        private ImageView mDeleteImgView;

        public FaveHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));
            mDatabase = FirebaseDatabase.getInstance().getReference("repositories");

            mNameTextView = (TextView) itemView.findViewById(R.id.repo_name_tv);
            mLangTextView = (TextView) itemView.findViewById(R.id.repo_language_tv);
            mStarCountTextView = (TextView) itemView.findViewById(R.id.stars_tv);
            mWatchCountTextView = (TextView) itemView.findViewById(R.id.watchers_tv);
            mDeleteImgView = (ImageView) itemView.findViewById(R.id.fave_image_view);
        }

        public void bind(Repository repo) {

            mRepo = repo;

            mNameTextView.setText(repo.getName());
            mLangTextView.setText(repo.getLanguage());
            mStarCountTextView.setText(repo.getStargazersCount().toString());
            mWatchCountTextView.setText(repo.getWatchersCount().toString());
            
            mDeleteImgView.setImageResource(R.drawable.ic_delete_black_24dp);
            mDeleteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mDatabase.child(String.valueOf(mRepo.getId()))
                            .removeValue();
                    mFaveRepos.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
