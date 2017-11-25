package com.plweegie.android.squash.utils;

import android.content.res.Resources;

import com.plweegie.android.squash.R;

public class AuthUtils {
    public static final String CLIENT_ID = Resources.getSystem().getString(R.string.client_id);
    public static final String CLIENT_SECRET = Resources.getSystem().getString(R.string.client_secret);

    public static final String PREFERENCE_NAME = "oauth_token";
}
