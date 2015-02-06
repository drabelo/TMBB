package com.example.tmbb;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends ActionBarActivity {
    private static final String TAB_1_TAG = "list";
    private static final String TAB_2_TAG = "measurements";
    // Fragment TabHost as mTabHost
    private FragmentTabHost mTabHost;
    // Tab back stacks
    private HashMap<TabType, Stack<String>> backStacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);


        // Create tabs


        mTabHost.addTab(mTabHost.newTabSpec(TAB_1_TAG).setIndicator("Threads"),
                ListContainerFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_2_TAG).setIndicator("High Scores"),
                meaFrag.class, null);

    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (currentTabTag.equals(TAB_1_TAG)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_1_TAG)).popFragment();
        } else if (currentTabTag.equals(TAB_2_TAG)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_2_TAG)).popFragment();
        }
        if (!isPopFragment) {
            finish();
        }
    }


    enum TabType {
        LIST, MEASUREMENTS
    }

}


