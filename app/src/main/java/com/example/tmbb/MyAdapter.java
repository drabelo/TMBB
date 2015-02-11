package com.example.tmbb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Person> {

    public MyAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MyAdapter(Context context, int resource, List<Person> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_item, null);

        }

        Person p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.contactNameText);
            CircularImageView tt1 = (CircularImageView) v.findViewById(R.id.contactPhoto);

            if (tt != null) {
                tt.setText(p.getName());
            }
            if (tt1 != null) {

                tt1.setImageBitmap(p.contact_thumbnail);
            }

        }

        return v;

    }
}