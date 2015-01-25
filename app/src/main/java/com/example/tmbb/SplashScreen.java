package com.example.tmbb;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class SplashScreen extends Activity {


    //INITIAL HOLD FOR NUMBERS

    Map<String, Person> personsMap = new HashMap<String, Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Just create simple XML layout with i.e a single ImageView or a custom layout
        setContentView(R.layout.splash_screen_layout);


        new PrefetchData().execute();

    }


    public void readInbox() throws Exception {
        Long start = System.currentTimeMillis();
        Cursor cursor = this.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"address", "body", "date"}, null, null, null);
        cursor.moveToFirst();
        do {
            String address = "";
            String body = "";
            String date = "";
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {

                //Gets COlumns
                address = cursor.getString(0);
                body = cursor.getString(1);
                date = cursor.getString(2);
                address = address.replaceAll("[^?0-9]+", "");
                if (address.startsWith("1"))
                    address = address.substring(1);


                String newD = convertDate(date, "MM-dd-yyyy");

                //Attempts to add new person if they dont exsits, also adds body

                if (personsMap.containsKey(address)) {
                    personsMap.get(address).addNewReceived(new Body(newD, body));
                } else {
                    personsMap.put(address, new Person(address));
                    personsMap.get(address).addNewReceived(new Body(newD, body));
                }
            }


        } while (cursor.moveToNext());

        cursor.close();
        for (String address : personsMap.keySet()) {
            String name = getContactName(this, address);
            if (name != null) {
                personsMap.get(address).setName(name);
            }
        }


        Long end = System.currentTimeMillis();
        Log.d("DEBUG INBOX", " " + (end - start));

    }

    public static String getContactName(Context context, String phoneNumber) {
        Long start = System.currentTimeMillis();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        Long end = System.currentTimeMillis();
        Log.d("DEBUG ContactName", " " + (end - start));

        return contactName;
    }

    public void readOutbox() throws Exception {
        Long start = System.currentTimeMillis();

        Cursor cursor = this.getContentResolver().query(Uri.parse("content://sms/sent"), new String[]{"address", "body", "date"}, null, null, null);
        cursor.moveToFirst();

        do {
            String address = "";
            String body = "";
            String date = "";
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {

                //Gets COlumns
                address = cursor.getString(0);
                body = cursor.getString(1);
                date = cursor.getString(2);
                address = address.replaceAll("[^?0-9]+", "");
                if (address.startsWith("1"))
                    address = address.substring(1);

                String newD = convertDate(date, "MM-dd-yyyy");


                if (personsMap.containsKey(address)) {


                    personsMap.get(address).addNewSent(new Body(newD, body));
                } else {
                    personsMap.put(address, new Person(address));
                    personsMap.get(address).addNewSent(new Body(newD, body));
                }
            }


        } while (cursor.moveToNext());

        Long end = System.currentTimeMillis();
        Log.d("DEBUG Outbox", " " + (end - start));

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }


    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */

            try {
                readInbox();
                readOutbox();
            } catch (Exception e1) {

            }

            FileOutputStream fos = null;
            try {
                fos = openFileOutput("persons.txt", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(personsMap);
                os.close();
                Log.d("DEBUG", "WROTE");
                Log.d("DEBUG", "WROTE" + personsMap.size());

            } catch (Exception e1) {
                Log.d("DEBUG", "IO1");
                e1.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            Intent intent = new Intent(SplashScreen.this, SmsListActivity.class);
            startActivity(intent);
            finish();
        }

    }


}