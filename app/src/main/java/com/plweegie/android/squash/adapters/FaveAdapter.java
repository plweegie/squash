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
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.RepoEntry;
import com.plweegie.android.squash.ui.FaveDeleteDialog;
import com.plweegie.android.squash.ui.LastCommitDetailsActivity;


public class FaveAdapter extends BaseGithubAdapter {

    private static final String DELETE_DIALOG = "delete_dialog";

    private FaveAdapterOnClickHandler mClickHandler;

    public FaveAdapter(Context context, FaveAdapterOnClickHandler clickHandler) {
        super(context);
        mClickHandler = clickHandler;
    }

    @Override
    public FaveHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new FaveHolder(inflater, vg, R.layout.repo_view_holder);
    }

    public interface FaveAdapterOnClickHandler {
        void onItemClick(long repoId);
    }

    public class FaveHolder extends BaseViewHolder {

        private ImageView mDeleteImgView;
        private ImageView mInfoImageView;

        public FaveHolder(LayoutInflater inflater, ViewGroup parent,
                          int layoutResId) {
            super(inflater, parent, layoutResId);
            mDeleteImgView = itemView.findViewById(R.id.fave_image_view);
            mInfoImageView = itemView.findViewById(R.id.last_commit_image_view);
        }

        public void bind(RepoEntry repo) {

            super.bind(repo);
            mDeleteImgView.setImageResource(R.drawable.ic_delete_black_24dp);
            mDeleteImgView.getDrawable()
                    .setColorFilter(mContext.getResources().getColor(R.color.colorAlert),
                            PorterDuff.Mode.SRC_IN);
            mDeleteImgView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                FaveDeleteDialog dialog = new FaveDeleteDialog();
                dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(),
                        DELETE_DIALOG);
                dialog.setClickHandler(mClickHandler, mRepos.get(position).getRepoId());
            });

            mInfoImageView.setImageResource(R.drawable.ic_description_24dp);
            mInfoImageView.setOnClickListener(view -> {
                Intent intent = LastCommitDetailsActivity.newIntent(mContext,
                        new String[] {repo.getOwner().getLogin(), repo.getName()});
                mContext.startActivity(intent);
            });
        }
    }
}

