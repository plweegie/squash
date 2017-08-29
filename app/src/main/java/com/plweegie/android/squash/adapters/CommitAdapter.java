/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import com.plweegie.android.squash.utils.Commit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        return new CommitHolder(inflater, vg, R.layout.commit_view_holder);
    }

    @Override
    public void onBindViewHolder(CommitHolder vh, int i) {
        Commit commit = mCommits.get(i);
        vh.bind(commit);
    }

    @Override
    public int getItemCount() {
        return mCommits.size();
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
            
            mMessageTextView = (TextView) itemView.findViewById(R.id.commit_message_tv);
            mInfoTextView = (TextView) itemView.findViewById(R.id.commit_info_tv);
        }
        
        public void bind(Commit commit) {
            mMessageTextView.setText(commit.getCommitBody().getMessage()
                    .split("\n")[0]);
            mInfoTextView.setText(buildCommitInfo(commit));
        }
        
        private String convertDate(String date) throws ParseException {
            String dayDate = date.split("T")[0];
            DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat targetFormat = new SimpleDateFormat("dd MMMM, yyyy");
            
            Date commitDate = sourceFormat.parse(dayDate);
            return targetFormat.format(commitDate);
        }
        
        private CharSequence buildCommitInfo(Commit commit) {
            
            CharSequence result;
            
            String authorId = commit.getAuthor().getLogin();
            
            String date = commit.getCommitBody().getCommitBodyAuthor().getDate();
            String formattedDate = "";
            
            try {
                formattedDate = convertDate(date);
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
