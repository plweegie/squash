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
import com.plweegie.android.squash.utils.DateUtils;
import com.plweegie.android.squash.utils.GitHubService;
import com.plweegie.android.squash.utils.QueryPreferences;

import java.text.ParseException;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jan on 25/09/17.
 */

public class LastCommitDetailsActivity extends AppCompatActivity {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static final String TEXT_VIEW_CONTENTS = "textViewContents";
    private static final String EXTRA_REPO_PROPS = "repoPropsExtra";

    private String[] mRepoProps;
    private TextView mMessageTextView;
    private TextView mInfoTextView;
    private TextView mDateTextView;

    private OkHttpClient mClient;
    private Retrofit mRetrofit;
    private GitHubService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Cache cache = new Cache(this.getCacheDir(), 5 * 1024 * 1024);
        mClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .client(mClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = mRetrofit.create(GitHubService.class);

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
        Call<List<Commit>> call = mService.getCommits(mRepoProps[0], mRepoProps[1], 1);

        call.enqueue(new Callback<List<Commit>>() {
            @Override
            public void onResponse(Call<List<Commit>> call, Response<List<Commit>> response) {
                Commit commit = response.body().get(0);
                mMessageTextView.setText(commit.getCommitBody().getMessage()
                        .split("\n")[0]);
                mInfoTextView.setText(buildCommitInfo(commit));
                mDateTextView.setText(buildCommitDate(commit));
                QueryPreferences.setLastResultSha(LastCommitDetailsActivity.this, commit.getSha());
            }

            @Override
            public void onFailure(Call<List<Commit>> call, Throwable t) {
                Log.e("LastCommitFragment", "Retrofit error: " + t);
            }
        });
    }

    private CharSequence buildCommitInfo(Commit commit) {

        CharSequence result;
        String authorId;

        if (commit.getAuthor() == null) {
            authorId = "null";
        } else {
            authorId = commit.getAuthor().getLogin();
        }

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