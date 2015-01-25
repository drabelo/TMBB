package com.example.tmbb;

import android.app.Fragment;

public class SmsListActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new SmsListFragment();
    }

}
