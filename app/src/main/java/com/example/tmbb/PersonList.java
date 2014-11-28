package com.example.tmbb;

import java.util.ArrayList;
import java.util.UUID;


import android.content.Context;


public class PersonList {
	
	private ArrayList<Person> mPersons;
	private static PersonList sPersonList;
	private Context mAppContext;

	private PersonList(Context appContext) {
	
	mAppContext = appContext;
	mPersons = new ArrayList<Person>();
	}
	
	public Person getPerson(String name) {
		for (Person p : mPersons) {
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}
	
	public static PersonList get(Context c) {
		if (sPersonList == null) {
			sPersonList = new PersonList(c.getApplicationContext());
		}
		return sPersonList;
	}

	public ArrayList<Person> getPersons() {
		return mPersons;
	}

	
	

}
