/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.plweegie.android.squash.CommitListFragment;
import com.plweegie.android.squash.FaveListFragment;
import com.plweegie.android.squash.R;
import com.plweegie.android.squash.RepoListFragment;


public class GithubPagerAdapter extends FragmentStatePagerAdapter {
    
    private Context mContext;
    private int[] mTabTitles = new int[] {R.string.list_repos, R.string.list_faves,
        R.string.list_commits};
    
    public GithubPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }
    
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return RepoListFragment.newInstance();
            case 1:
                return FaveListFragment.newInstance();
            case 2:
                return CommitListFragment.newInstance();
            default:
                throw new IllegalArgumentException("Position error in ViewPager");
        }
    }
    
    @Override
    public int getCount() {
        return 3;
    }
    
    @Override
    public String getPageTitle(int position) {
        return mContext.getResources().getString(mTabTitles[position]);
    }
}
