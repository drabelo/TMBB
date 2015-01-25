package com.example.tmbb;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import java.util.ArrayList;

public class DetailViewActivity extends SingleFragmentActivity {
    private ArrayList<Person> mCrimes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected Fragment createFragment() {
        return new DetailViewFragment();
    }

}
