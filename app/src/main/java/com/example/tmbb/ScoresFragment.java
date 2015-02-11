package com.example.tmbb;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;


public class ScoresFragment extends Fragment {

    MaterialListView mListView;

    ArrayList<String> bestFriends = new ArrayList<String>();
    TextView bf1;
    TextView bf2;
    TextView bf3;
    TextView receivedSMSCount;
    TextView sentSMSCount;
    int sentSMS;
    int receivedSMS;

    TextView receivedMMSCount;
    TextView sentMMSCount;
    int sentMMS;
    int receivedMMS;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.scores_fragment, container, false);
        countBFFs();
        countSMS();
        countMMS();


        bf1 = (TextView) v.findViewById(R.id.bf1);
       bf2 = (TextView) v.findViewById(R.id.bf2);
        bf3 = (TextView) v.findViewById(R.id.bf3);

        sentSMSCount = (TextView) v.findViewById(R.id.sentSMS);
        receivedSMSCount = (TextView) v.findViewById(R.id.receivedSMS);
        sentSMSCount.setText(sentSMS + "");
        receivedSMSCount.setText(receivedSMS + "");

        sentMMSCount = (TextView) v.findViewById(R.id.MMSsent);
        receivedMMSCount = (TextView) v.findViewById(R.id.MMSreceived);
        sentMMSCount.setText(sentMMS + "");
        receivedMMSCount.setText(receivedMMS + "");

        bf1.setText(bestFriends.get(0));
        bf2.setText(bestFriends.get(1));
        bf3.setText(bestFriends.get(2));




        return v;
    }

    public static ScoresFragment newInstance(int page, String title) {
        ScoresFragment fragment = new ScoresFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public void countBFFs(){
        Cursor cursor1 = getActivity().getContentResolver().query(Telephony.Sms.Conversations.CONTENT_URI, new String[] {"thread_id", "msg_count"}, null, null, "msg_count DESC");
        cursor1.moveToFirst();
        for(int i = 0; i <= 2; i++){
            bestFriends.add(convertThreadIDtoContactName(cursor1.getString(0)));
            cursor1.moveToNext();
        }
    }



    public String convertThreadIDtoContactName(String thread_id){
        Cursor cursor1 = getActivity().getContentResolver().query(Telephony.Threads.CONTENT_URI, new String[]{"address"}, "thread_id = "+thread_id, null, null);
        cursor1.moveToFirst();
        String address = cursor1.getString(0);
        String name = getContactName(getActivity(), address);
        return name;
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

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public void countSMS(){
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(Telephony.Sms.Sent.CONTENT_URI, null, null, null, null);
        sentSMS = cursor.getCount();
        Cursor cursor2 = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        receivedSMS = cursor2.getCount() - sentSMS;
    }

    public void countMMS(){
        String[] args = { "image/jpeg", "image/bmp" , "image/gif" , "image/jpg", "image/png"};
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(Telephony.Mms.Inbox.CONTENT_URI, null, "text_only=0", null, null);
        Log.d("Debug", DatabaseUtils.dumpCursorToString(cursor));
        sentMMS = cursor.getCount();
        Cursor cursor2 = cr.query(Telephony.Mms.CONTENT_URI, null, "text_only=0", null, null);
        receivedMMS = cursor2.getCount() - sentMMS;
    }




}
