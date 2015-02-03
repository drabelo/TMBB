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
import android.provider.Telephony;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;


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

public class SplashScreen extends Activity {


    /**
     * Generates the LinkedHashmap that holds all of the data at start time
     *
     * @author Dailton Rabelo
     */

    //Hashmap holding all contacts names and number info
    Map<String, Person> personsMap = new LinkedHashMap<String, Person>();

    /**
     * Method for processing the template with velocity.
     *
     * @param context     the class.
     * @param phoneNumber the phone number
     * @return Name the name of the contact
     */
    public static String getContactName(Context context, String phoneNumber) {
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

        return contactName;
    }


    /**
     * Method for processing the template with velocity.
     *
     * @param dateInMilliseconds the date in miliseconds
     * @param dateFormat         the format of the date
     * @return convertedDate The converted date
     */
    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Just create simple XML layout with i.e a single ImageView or a custom layout
        setContentView(R.layout.splash_screen_layout);


        new PrefetchData().execute();

    }

    /**
     * Method for processing the read segment of the SMS/MMS database
     */
    public void readInbox() throws Exception {

        //counting SMS inbox
        //setting cursor to read SMS
        Cursor cursor = this.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[]{"address", "body", "date"}, null, null, null);
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


        //counting MMS inbox

        ContentResolver cr = getContentResolver();
        //setting cursor to read MMS
        Cursor cursor2 = cr.query(Telephony.Mms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Mms.Inbox._ID, Telephony.Mms.Inbox.DATE}, // Select body text
                null,
                null,
                null); // Default sort order

        cursor2.moveToFirst();
        Log.d("DebugCursor", cursor2.getCount() + "");

        do {

            for (int idx = 0; idx < cursor2.getCount() - 1; idx++) {

                //getting ID of MMS
                String id = cursor2.getString(0);
                String date = cursor2.getString(1);

                //Getting MMS info from ID
                Cursor cursor3 = cr.query(Uri.parse("content://mms/" + id + "/addr"), // Official CONTENT_URI from docs
                        new String[]{Telephony.Mms.Addr.ADDRESS}, // Select body text
                        null,
                        null,
                        null);

                cursor3.moveToFirst();
                String address = cursor3.getString(0);
                address = address.replaceAll("[^?0-9]+", "");
                if (address.startsWith("1"))
                    address = address.substring(1);


                String newD = convertDate(date + "000", "MM-dd-yyyy");

                //Attempts to add new person if they dont exsits, also adds body
                Log.d("DebuggingMMS", "" + newD + "  " + address);

                if (personsMap.containsKey(address)) {
                    personsMap.get(address).addNewReceived(new Body(newD, "mms"));
                    Log.d("DebuggingMMS", "HIT");

                } else {
                    personsMap.put(address, new Person(address));
                    personsMap.get(address).addNewReceived(new Body(newD, "mms"));
                    Log.d("DebuggingMMS", "FAIL");

                }
            }


        } while (cursor2.moveToNext());

        cursor2.close();

        for (String address : personsMap.keySet()) {
            //getContactPicture(this, address);
            String name = getContactName(this, address);
            if (name != null) {
                personsMap.get(address).setName(name);
            }
        }

    }

    /**
     * Method for processing the sent segment of the SMS/MMS database
     */
    public void readOutbox() throws Exception {

        //initiate cursor for getting the SMS date
        Cursor cursor = getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, new String[]{"address", "body", "date"}, null, null, null);
        cursor.moveToFirst();

        do {
            String address = "";
            String body = "";
            String date = "";
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {

                //Gets Columns
                address = cursor.getString(0);
                body = cursor.getString(1);
                date = cursor.getString(2);
                address = address.replaceAll("[^?0-9]+", "");
                if (address.startsWith("1")) {
                    address = address.substring(1);
                }
                String newD = convertDate(date, "MM-dd-yyyy");

                if (personsMap.containsKey(address)) {
                    personsMap.get(address).addNewSent(new Body(newD, body));
                } else {
                    personsMap.put(address, new Person(address));
                    personsMap.get(address).addNewSent(new Body(newD, body));
                }
            }
        } while (cursor.moveToNext());


        ContentResolver cr = getContentResolver();

        //Initiate cursor for getting the MMS data
        Cursor cursor2 = cr.query(Telephony.Mms.Sent.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Mms.Sent._ID, Telephony.Mms.Sent.DATE}, // Select body text
                null,
                null,
                null); // Default sort order

        cursor2.moveToFirst();
        Log.d("DebugCursor", cursor2.getCount() + "");

        do {

            for (int idx = 0; idx < cursor2.getCount() - 1; idx++) {

                //getting ID of MMS
                String id = cursor2.getString(0);
                String date = cursor2.getString(1);

                //Getting MMS info from ID
                Cursor cursor3 = cr.query(Uri.parse("content://mms/" + id + "/addr"), // Official CONTENT_URI from docs
                        new String[]{Telephony.Mms.Addr.ADDRESS, "CONTACT_ID"}, // Select body text
                        null,
                        null,
                        null);


                cursor3.moveToFirst();
                cursor3.moveToNext();
                String address = cursor3.getString(0);
                String test = cursor3.getString(0);
                Log.d("Debug", "" + test);


                String newD = convertDate(date + "000", "MM-dd-yyyy");


                if (personsMap.containsKey(address)) {
                    personsMap.get(address).addNewSent(new Body(newD, "mms"));

                } else {
                    personsMap.put(address, new Person(address));
                    personsMap.get(address).addNewSent(new Body(newD, "mms"));

                }
            }


        } while (cursor2.moveToNext());

        cursor2.close();


        //Goes through every person in the hashmap and sets there name using the address(key)
        for (String address : personsMap.keySet()) {
            //getContactPicture(this, address);
            String name = getContactName(this, address);
            if (name != null) {
                personsMap.get(address).setName(name);
            } else {
                personsMap.get(address).setName(address);
            }
        }


    }


    /**
     * Method to do tasks in the background!
     */
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
            //Serializes the hashmap!
            try {

                fos = openFileOutput("persons.txt", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(personsMap);
                os.close();

            } catch (Exception e1) {
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