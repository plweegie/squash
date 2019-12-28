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

package com.plweegie.android.squash.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.plweegie.android.squash.App;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.data.Commit;
import com.plweegie.android.squash.rest.GitHubService;
import com.plweegie.android.squash.utils.DateUtils;

import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LastCommitDetailsActivity extends AppCompatActivity {

    private static final String TEXT_VIEW_CONTENTS = "textViewContents";
    private static final String EXTRA_REPO_PROPS = "repoPropsExtra";

    @Inject
    GitHubService mService;

    private String[] mRepoProps;
    private TextView mMessageTextView;
    private TextView mInfoTextView;
    private TextView mDateTextView;

    private Disposable mDisposable;

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
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
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
        Observable<List<Commit>> call = mService.getCommitsObservable(mRepoProps[0], mRepoProps[1], 1);

        mDisposable = call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Commit>>() {
                    @Override
                    public void onNext(List<Commit> commits) {
                        Commit commit = commits.get(0);
                        mMessageTextView.setText(commit.getCommitBody().getMessage()
                                .split("\n")[0]);
                        mInfoTextView.setText(buildCommitInfo(commit));
                        mDateTextView.setText(buildCommitDate(commit));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Crashlytics.log(1, "LastCommitDetails", "Retrofit error");
                        Crashlytics.logException(e);
                    }

                    @Override
                    public void onComplete() {}
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
        String date = commit.getCommitBody().getCommitter().getDate();
        String formattedDate = "";

        try {
            formattedDate = DateUtils.changeDateFormats(date);
        } catch (ParseException e) {
            Crashlytics.log("Date parser error");
            Crashlytics.logException(e);
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
