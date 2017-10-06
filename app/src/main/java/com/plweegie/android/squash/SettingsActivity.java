package com.plweegie.android.squash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jan on 06/10/17.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
