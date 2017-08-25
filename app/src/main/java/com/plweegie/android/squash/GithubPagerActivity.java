/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plweegie.android.squash;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.plweegie.android.squash.adapters.GithubPagerAdapter;

public class GithubPagerActivity  extends AppCompatActivity {
    
    private ViewPager mViewPager;
    private GithubPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.view_pager);
        
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.list_tabs);
        mAdapter = new GithubPagerAdapter(getSupportFragmentManager(),
                GithubPagerActivity.this);
        
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
