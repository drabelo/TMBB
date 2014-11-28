package com.example.tmbb;


import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SmsListFragment extends ListFragment {
	Set<String> set = new HashSet<String>();
	
	ArrayList<Person> persons;
	   ArrayList<String> names = new ArrayList<String>();
	   Pattern p = Pattern.compile("-?\\d+");

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		persons = PersonList.get(getActivity()).getPersons();


        if (savedInstanceState != null) {
            try {
                names = savedInstanceState.getStringArrayList("NAMES");
                persons = ((ArrayList<Person>) savedInstanceState.getSerializable("PERSONS"));
            }catch (Exception e){
                Log.d("DEBUG", e.getMessage());
            }
            Log.d("DEBUG", "onSaved");
            Log.d("DEBUG", names.toString());
            Log.d("DEBUG", persons.toString());


            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            names);
            setListAdapter(adapter);
        }else {
            readInbox();
            readOutbox();
        }

		
		
		   

	}

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("DEBUG", "SAVING");
        savedInstanceState.putStringArrayList("NAMES", names);
        savedInstanceState.putSerializable("PERSONS", persons);

    }
	
	public void readInbox(){
		Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] {"address", "body", "date"}, null, null, null);
		   cursor.moveToFirst();
		   do{
		      String address = "";
		      String body = "";
		      String date = "";
		      for(int idx=0;idx<cursor.getColumnCount();idx++)
		      {
		          
		    	  //Gets COlumns
		    	  address =  cursor.getString(0);
		          body =  cursor.getString(1);
		          date = cursor.getString(2);
		          address = address.replaceAll("[^?0-9]+", ""); 
		          if(address.startsWith("1"))
		        	  address = address.substring(1);
		          
		          
		          String newD = convertDate(date,"MM-dd-yyyy");

		         
		          
		          
		          //Attempts to add new person if they dont exsits, also adds body
		          set.add(address);
		          if(persons.contains(new Person(address))){
		        	  int index = persons.indexOf(new Person(address));
		        	  persons.get(index).addNewReceived(new Body(newD, body));
		          }else{
		          Person p = new Person(address);
		          p.addNewReceived(new Body(date, body));
		          persons.add(p);
		          }
		      }


		   }while(cursor.moveToNext());

		   cursor.close();
		   ArrayList<String> names = new ArrayList<String>();
		   for( String s : set){
			   String name = getContactName(getActivity(), s);
			   if(name != null) {names.add(name);}
			   for(Person p : persons)
			   {
				   if(s.equals(p.getPhoneNumber())){

					   p.setName(name);
					   
			   }
		   }
		   }
		   
		   Collections.sort(names);
		   
		   ArrayAdapter<String> adapter =
				   new ArrayAdapter<String>(getActivity(),
				   android.R.layout.simple_list_item_1,
				   names);
				   setListAdapter(adapter);
		   
	}
	
	public void readOutbox(){
		Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://sms/sent"), new String[] {"address", "body", "date"}, null, null, null);
		   cursor.moveToFirst();

		   do{
			      String address = "";
			      String body = "";
			      String date = "";
		      for(int idx=0;idx<cursor.getColumnCount();idx++)
		      {
		          
		    	//Gets COlumns
		    	  address =  cursor.getString(0);
		          body = cursor.getString(1);
		          date = cursor.getString(2);
		          address = address.replaceAll("[^?0-9]+", ""); 
		          if(address.startsWith("1"))
		        	  address = address.substring(1);
		          
		          String newD = convertDate(date,"MM-dd-yyyy");

		          
		          
		          if(persons.contains(new Person(address))){
		        	  int index = persons.indexOf(new Person(address));
		        	  persons.get(index).addNewSent(new Body(newD, body));
		          }else{
			          Person p = new Person(address);
			          if(body != null && date != null){
			          p.addNewReceived(new Body(date, body));
			          persons.add(p);
			          }
		          }
		          
		          
		          
		          
		      }

		   }while(cursor.moveToNext());
		   
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		String name =  (String)getListAdapter().getItem(position);
		Intent i = new Intent(getActivity(), DetailViewActivity.class);

		i.putExtra("PERSON_ID", name);
		i.putExtra("PERSON_ARRAY", persons);
		startActivity(i);
	}

	public static String getContactName(Context context, String phoneNumber) {
	    ContentResolver cr = context.getContentResolver();
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
	    if (cursor == null) {
	        return null;
	    }
	    String contactName = null;
	    if(cursor.moveToFirst()) {
	        contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	    }

	    if(cursor != null && !cursor.isClosed()) {
	        cursor.close();
	    }

	    return contactName;
	}
	
	public static String convertDate(String dateInMilliseconds,String dateFormat) {
	    return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
	}

}
