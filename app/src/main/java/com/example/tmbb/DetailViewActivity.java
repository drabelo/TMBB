package com.example.tmbb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;


/**
 * This is the TextMeBack Android App.
 * It was created to be able to parse the android SMS/MMS database
 * and count how many times we interact with a specific contact. Right now
 * the only options for viewing are overall sent/recieved and the option of seeing
 * sent/recieved per day for the past two weeks. More features might be coming soon such
 * as measurements which show the highest average for who you text, and maybe it can
 * show text patterns that are declining.
 *
 * @author Dailton Rabelo
 */

public class DetailViewActivity extends SingleFragmentActivity {


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
