package com.plweegie.android.squash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.plweegie.android.squash.auth.GithubOauth;
import com.plweegie.android.squash.auth.ResultCode;
import com.plweegie.android.squash.utils.AuthUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jan on 25/11/17.
 */

public class LoginActivity extends AppCompatActivity {

    private Button mLoginBtn;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;

        mLoginBtn = findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GithubOauth.Builder()
                        .withClientId(AuthUtils.CLIENT_ID)
                        .withClientSecret(AuthUtils.CLIENT_SECRET)
                        .withContext(mActivity)
                        .withScopeList(new ArrayList(Arrays.asList("public_repo")))
                        .packageName("com.plweegie.android.squash")
                        .nextActivity("com.plweegie.android.squash.GithubPagerActivity")
                        .debug(true)
                        .execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ResultCode.SUCCESS) {
            if (requestCode == GithubOauth.REQUEST_CODE) {
                finish();
            }
        }
    }
}
