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

package com.plweegie.android.squash;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastCommitDetailsActivity extends AppCompatActivity {

    private static final String TEXT_VIEW_CONTENTS = "textViewContents";
    private static final String EXTRA_REPO_PROPS = "repoPropsExtra";

    @Inject
    GitHubService mService;

    private String[] mRepoProps;
    private TextView mMessageTextView;
    private TextView mInfoTextView;
    private TextView mDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((App) getApplication()).getNetComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commit_view);

        mRepoProps = getIntent().getStringArrayExtra(EXTRA_REPO_PROPS);

        mMessageTextView = findViewById(R.id.commit_message_tv);
        mInfoTextView = findViewById(R.id.commit_info_tv);
        mDateTextView = findViewById(R.id.commit_date_tv);

        if (savedInstanceState != null && savedInstanceState.containsKey(TEXT_VIEW_CONTENTS)) {
            mMessageTextView.setText(savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)[0]);
            mInfoTextView.setText(savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)[1]);
            mDateTextView.setText(savedInstanceState.getCharSequenceArray(TEXT_VIEW_CONTENTS)[2]);
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequenceArray(TEXT_VIEW_CONTENTS, new CharSequence[]{
                mMessageTextView.getText(),
                mInfoTextView.getText(),
                mDateTextView.getText()});
    }

    private void updateUI() {
        String authToken = QueryPreferences.getStoredAccessToken(this);
        Call<List<Commit>> call = mService.getCommits(mRepoProps[0], mRepoProps[1], 1,
                authToken);

        call.enqueue(new Callback<List<Commit>>() {
            @Override
            public void onResponse(Call<List<Commit>> call, Response<List<Commit>> response) {
                Commit commit = response.body().get(0);
                mMessageTextView.setText(commit.getCommitBody().getMessage()
                        .split("\n")[0]);
                mInfoTextView.setText(buildCommitInfo(commit));
                mDateTextView.setText(buildCommitDate(commit));
            }

            @Override
            public void onFailure(Call<List<Commit>> call, Throwable t) {
                Log.e("LastCommitFragment", "Retrofit error: " + t);
            }
        });
    }

    private CharSequence buildCommitInfo(Commit commit) {

        CharSequence result;

        String authorId = commit.getCommitBody().getCommitBodyAuthor().getName();
        String info = this.getResources().getString(R.string.commit_info, authorId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(info);
        }
        return result;
    }

    private CharSequence buildCommitDate(Commit commit) {
        String date = commit.getCommitBody().getCommitBodyAuthor().getDate();
        String formattedDate = "";

        try {
            formattedDate = DateUtils.changeDateFormats(date);
        } catch (ParseException e) {
            Log.e("CommitAdapter", "Date parser error: " + e);
        }

        String result = this.getResources().getString(R.string.commit_date, formattedDate);
        return result;
    }

    public static Intent newIntent(Context packageContext, String[] repoProps) {
        Intent intent = new Intent(packageContext, LastCommitDetailsActivity.class);
        intent.putExtra(EXTRA_REPO_PROPS, repoProps);
        return intent;
    }
}
