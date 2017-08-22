/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.support.v4.app.Fragment;

public class CommitListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return CommitListFragment.newInstance();
    }
}
