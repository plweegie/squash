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
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.utils.DateUtils;

import java.text.ParseException;
import java.util.List;


public class CommitAdapter extends RecyclerView.Adapter<CommitAdapter.CommitHolder> {
    
    private List<Commit> mCommits;
    private Context mContext;
    
    public CommitAdapter(Context context, List<Commit> commits) {
        mContext = context;
        mCommits = commits;
    }

    @Override
    public CommitHolder onCreateViewHolder(ViewGroup vg, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new CommitHolder(inflater, vg, R.layout.commit_view);
    }

    @Override
    public void onBindViewHolder(CommitHolder vh, int i) {
        Commit commit = mCommits.get(i);
        vh.bind(commit);
    }

    @Override
    public int getItemCount() {
        return mCommits == null ? 0 : mCommits.size();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setContent(List<Commit> commits) {
        mCommits = commits;
    }
    
    public class CommitHolder extends RecyclerView.ViewHolder {
        
        private TextView mMessageTextView;
        private TextView mInfoTextView;
        
        public CommitHolder(LayoutInflater inflater, ViewGroup parent,
                int layoutResId) {
            super(inflater.inflate(layoutResId, parent, false));
            
            mMessageTextView = itemView.findViewById(R.id.commit_message_tv);
            mInfoTextView = itemView.findViewById(R.id.commit_info_tv);
        }
        
        public void bind(Commit commit) {
            mMessageTextView.setText(commit.getCommitBody().getMessage()
                    .split("\n")[0]);
            mInfoTextView.setText(buildCommitInfo(commit));
        }
        
        private CharSequence buildCommitInfo(Commit commit) {
            
            CharSequence result;
            
            String authorId = commit.getCommitBody().getCommitBodyAuthor().getName();
            
            String date = commit.getCommitBody().getCommitBodyAuthor().getDate();
            String formattedDate = "";
            
            try {
                formattedDate = DateUtils.changeDateFormats(date);
            } catch (ParseException e) {
                Log.e("CommitAdapter", "Date parser error: " + e);
            }
            
            String info = mContext.getResources().getString(R.string.commit_info,
                    authorId, formattedDate);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                result = Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(info);
            }
            return result;
        }
    }
}
