package com.example.tmbb;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


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

public class SmsListFragment extends ListFragment {


    //MaterialList that holds all the cards
    MaterialListView mListView;
    ArrayList<holder> addresses = new ArrayList<holder>();
    ArrayList<Person> names = new ArrayList<Person>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static SmsListFragment newInstance(int page, String title) {
        SmsListFragment fragmentFirst = new SmsListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



            fillArray();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        /** Creating an array adapter to store the list of countries **/
        MyAdapter adapter = new MyAdapter(inflater.getContext(), R.layout.contact_item, names);

        /** Setting the list adapter for the ListFragment */
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    /**
     * Method for getting the photo bitmap from the URI
     *
     * @param //photoDate the PHOTO_THUMB_URI
     * @return bitMap returns the thumbnail as a bitmap
     */
    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            // If Android 3.0 or later
            if (Build.VERSION.SDK_INT
                    >=
                    Build.VERSION_CODES.HONEYCOMB) {
                // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                thumbUri = Uri.parse(photoData);
            } else {
                // Prior to Android 3.0, constructs a photo Uri using _ID
                /*
                 * Creates a contact URI from the Contacts content URI
                 * incoming photoData (_ID)
                 */
                final Uri contactUri = Uri.withAppendedPath(
                        Contacts.CONTENT_URI, photoData);
                /*
                 * Creates a photo URI by appending the content URI of
                 * Contacts.Photo.
                 */
                thumbUri =
                        Uri.withAppendedPath(
                                contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = getActivity().getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
        }
        // In all cases, close the asset file descriptor
        finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = "";
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(contactName.length() < 2){
            contactName = phoneNumber;
        }


        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Person person =  (Person)getListAdapter().getItem(position);
        Intent i = new Intent(getActivity(), DetailViewActivity.class);
        i.putExtra("THREAD_ID", person.thread_id);
        i.putExtra("NAME", person.name);
        startActivity(i);
    }

    public void fillArray() {
        Cursor cursor1 = getActivity().getContentResolver().query(Telephony.Threads.CONTENT_URI, new String[]{"address", "thread_id"}, null, null, "date DESC");
        cursor1.moveToFirst();
        do {

            addresses.add(new holder(cursor1.getString(0), cursor1.getString(1)));


        } while (cursor1.moveToNext());


        for (holder temp : addresses) {
            String contactName = getContactName(getActivity(), temp.address);
            if (contactName != null){
                SimpleCard card = new SmallImageCard(getActivity());
                card.setDescription(contactName);
                card.setSecret(temp.thread_id);



//

                ContentResolver cr = getActivity().getContentResolver();
                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(temp.address));
                Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);
                Bitmap map;

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    if (cursor.getString(0) != null) {
                        map = loadContactPhotoThumbnail(cursor.getString(0));
                        names.add(new Person(contactName, map, temp.thread_id));
                        Drawable d = new BitmapDrawable(getResources(), map);

                        card.setDrawable(d);
                    } else {
                        names.add(new Person(contactName, BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.contact_thumbnail), temp.thread_id));
                    }
                } else {
                    names.add(new Person(contactName, BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.contact_thumbnail), temp.thread_id));


                }

                try {
                    mListView.add(card);
                } catch (Exception e) {
                }
                cursor.close();

            }

        }

        cursor1.close();
    }

    public class holder {
        public String getThread_id() {
            return thread_id;
        }

        public void setThread_id(String thread_id) {
            this.thread_id = thread_id;
        }

        public String getAddress() {
            return address;
        }


        public holder(String address, String thread_id) {
            this.address = address;
            this.thread_id = thread_id;
        }

        String address;

        public void setAddress(String address) {
            this.address = address;
        }

        String thread_id;
    }

}
