package com.plweegie.android.squash;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by jan on 06/10/17.
 */

public class SettingsFragment extends PreferenceFragment {
    public static final String KEY_PREF_SORT_BY_SETTING = "pref_sortBySetting";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
