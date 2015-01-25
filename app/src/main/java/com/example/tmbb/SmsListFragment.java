package com.example.tmbb;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SmsListFragment extends ListFragment {

    Map<String, Person> persons = new HashMap<String, Person>();
    ArrayList<String> names = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FileInputStream fis = getActivity().openFileInput("persons.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            Map<String, Person> simpleClass = (Map<String, Person>) is.readObject();
            is.close();
            persons = simpleClass;
            Log.d("DEBUG", "READ1" + persons.size());


            for (String address : persons.keySet()) {
                if (persons.get(address).getName().length() > 2) {
                    names.add(persons.get(address).getName());
                }
            }


            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            names);
            setListAdapter(adapter);


        } catch (Exception e) {
            Log.d("Debug", e.getMessage());
            e.printStackTrace();
        }


    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), DetailViewActivity.class);

        i.putExtra("PERSON_ID", name);


        startActivity(i);
    }


}
