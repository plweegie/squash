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
package com.plweegie.android.squash.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.plweegie.android.squash.R;
import com.plweegie.android.squash.ui.FaveListFragment;
import com.plweegie.android.squash.ui.RepoListFragment;


public class GithubPagerAdapter extends FragmentStatePagerAdapter {
    
    private Context mContext;
    private int[] mTabTitles = new int[] {R.string.list_repos, R.string.list_faves};
    
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
            default:
                throw new IllegalArgumentException("Position error in ViewPager");
        }
    }
    
    @Override
    public int getCount() {
        return 2;
    }
    
    @Override
    public String getPageTitle(int position) {
        return mContext.getResources().getString(mTabTitles[position]);
    }
}
