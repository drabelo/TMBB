package com.example.tmbb;

import java.util.UUID;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.text.format.DateFormat;

public class SmsListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new SmsListFragment();
	}

}
