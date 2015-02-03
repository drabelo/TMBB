package com.example.tmbb;


import android.app.Fragment;
import android.content.ContentResolver;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

public class SmsListFragment extends Fragment {

    //Hashmap that holds all the info
    Map<String, Person> persons = new HashMap<String, Person>();
    //Holds all of the names of the contacts
    ArrayList<String> names = new ArrayList<String>();
    //MaterialList that holds all the cards
    MaterialListView mListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_activity, parent, false);

        mListView = (MaterialListView) v.findViewById(R.id.material_listview);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String name = ((SimpleCard) mListView.getCard(position)).getDescription();
                Intent i = new Intent(getActivity(), DetailViewActivity.class);

                i.putExtra("PERSON_ID", name);


                startActivity(i);
            }
        });

        //Trying to read the hashmap from file!
        try {
            FileInputStream fis = getActivity().openFileInput("persons.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            Map<String, Person> simpleClass = (Map<String, Person>) is.readObject();
            is.close();
            persons = simpleClass;
            Log.d("DEBUG", "READ1" + persons.size());


            for (String address : persons.keySet()) {
                if (persons.get(address).getName().trim().length() < 2) {
                    persons.remove(address);
                }
            }


        } catch (Exception e) {
            Log.d("Debug", "" + e.getMessage());
            e.printStackTrace();
        }

        Log.d("Debug", names.toString());

        //trying to find contact thumbnail from address
        for (String address : persons.keySet()) {
            SimpleCard card = new SmallImageCard(getActivity());
            card.setDescription(persons.get(address).getName());


            ContentResolver cr = getActivity().getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);
            Bitmap map;

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                if (cursor.getString(0) != null) {
                    map = loadContactPhotoThumbnail(cursor.getString(0));

                    Drawable d = new BitmapDrawable(getResources(), map);

                    card.setDrawable(d);
                } else {
                    card.setDrawable(R.drawable.contact_thumbnail);
                }
            } else {
                card.setDrawable(R.drawable.contact_thumbnail);
            }

            try {
                mListView.add(card);
            } catch (Exception e) {
                Log.d("Debug", "" + e.getMessage());
            }

        }


        return v;
    }

    /**
     * Method for getting the photo bitmap from the URI
     *
     * @param photoDate the PHOTO_THUMB_URI
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


}
