package com.example.tmbb.Fragments;


import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tmbb.Body;
import com.example.tmbb.Model.Person;
import com.example.tmbb.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;

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
public class DetailViewFragment extends Fragment {
    //current person
    Person person = new Person();
    TextView textviewReceived;
    TextView textviewSent;
    TextView textviewName;

    //persons List
    List<Column> columns = new ArrayList<Column>();
    //List for axis
    List<AxisValue> axisValues = new ArrayList<AxisValue>();
    String thread_id = "not set";
    String name = "not set";

    //variables to make charts
    private ColumnChartView colChart;
    private ColumnChartData data;
    private PreviewColumnChartView previewChart;

    //Converts date in miliseconds using format to whatever format you want
    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //getting information from intents
        thread_id = (String) getActivity().getIntent()
                .getSerializableExtra("THREAD_ID");
        name = (String) getActivity().getIntent()
                .getSerializableExtra("NAME");


        try {
            readInbox();
            readOutbox();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_fragment, parent, false);
        try {
            textviewReceived = (TextView) v.findViewById(R.id.textViewReceived);
            textviewReceived.setText("" + person.received.size());

            textviewName = (TextView) v.findViewById(R.id.textviewName);
            textviewName.setText(name);
//
            textviewSent = (TextView) v.findViewById(R.id.textViewSent);
            textviewSent.setText("" + person.sent.size() + "\t");


            Log.d("Debbuging onview", person.received.size() + "   sent: " + person.sent.size());
            //creating Column Chart
            colChart = (ColumnChartView) v.findViewById(R.id.chart);
            getColumns();
            data.setColumns(columns);

            //creating Axis
            Axis axisX = new Axis();
            axisX.setValues(axisValues);
            axisX.setMaxLabelChars(4);
            axisX.setHasSeparationLine(true);

            data.setAxisXBottom(axisX);
            data.setStacked(true);
            colChart.setValueSelectionEnabled(true);
            colChart.setBackgroundColor(Color.parseColor("#E0F7FA"));
            colChart.setColumnChartData(data);
            colChart.setScrollEnabled(true);
            colChart.setZoomEnabled(false);
            colChart.setScrollEnabled(false);

            //generating preview
            previewChart = (PreviewColumnChartView) v.findViewById(R.id.chart_preview);

            // prepare preview data, is better to use separate deep copy for preview chart.
            // set color to grey to make preview area more visible.
            ColumnChartData previewData = new ColumnChartData(data);
            for (Column column : previewData.getColumns()) {
                for (SubcolumnValue value : column.getValues()) {
                    value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
                }
            }

            previewChart.setColumnChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(true);


        } catch (Exception e) {
            Log.d("DEBUGGING VIEW", e.getMessage() + "");
        }
        return v;
    }

    /**
     * This method parses the persons sent/received arraylists by date.
     * It generates all of the Columns for the column chart.
     */
    public void getColumns() {


        DateTime now = DateTime.now().plusDays(1);
        DateTime start = now;
        DateTime stop = now.minusDays(14);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTimeFormatter format = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTimeFormatter format2 = DateTimeFormat.forPattern("E");


        List<SubcolumnValue> values;

        int received = 0;
        int sent = 0;
        int curr = 0;


        data = new ColumnChartData(columns);


        while (!fmt.print(stop).equals(fmt.print(now))) {
            for (Body p : person.received) {
                if (format.print(stop).equals(p.date)) {
                    received++;
                }
            }

            for (Body p : person.sent) {
                if (format.print(stop).equals(p.date)) {
                    sent++;
                }
            }

            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(received, Color.parseColor("#56B7F1")));
            values.add(new SubcolumnValue(sent, 0xFF63CBB0));


            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
            AxisValue value = new AxisValue(curr, format2.print(stop).toCharArray());

            curr++;
            axisValues.add(value);


            stop = stop.plusDays(1);
            sent = 0;
            received = 0;
        }


    }

    public void readInbox() {

        //counting SMS inbox
        //setting cursor to read SMS
        Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[]{"date"}, "thread_id = " + thread_id, null, null);
        cursor.moveToFirst();
        Log.d("DEBUGGING CURSOR", DatabaseUtils.dumpCursorToString(cursor));

        do {
            String date = "";

            //Gets COlumns
            date = cursor.getString(0);

            Log.d("Debuging date", date);
            String newD = convertDate(date, "MM-dd-yyyy");
            try {
                person.addNewReceived(new Body(newD, "mms"));
            } catch (Exception e) {
                Log.d("Debugging", e.getMessage() + "");
            }

        } while (cursor.moveToNext());


        cursor.close();


        //counting MMS inbox

//        ContentResolver cr = getActivity().getContentResolver();
//        //setting cursor to read MMS
//        Cursor cursor2 = cr.query(Telephony.Mms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
//                new String[]{Telephony.Mms.Inbox.DATE}, // Select body text
//                null,
//                null,
//                null); // Default sort order
//        Log.d("Debug", DatabaseUtils.dumpCursorToString(cursor2));
//        cursor2.moveToFirst();
//
//        do {
//
//
//                //getting ID of MMS
//                String date = cursor2.getString(0);
//
//
//                String newD = convertDate(date + "000", "MM-dd-yyyy");
//
//                person.addNewReceived(new Body(newD, "mms"));
//
//
//
//
//        } while (cursor2.moveToNext());
//
//        cursor2.close();


    }

    /**
     * Method for processing the sent segment of the SMS/MMS database
     */
    public void readOutbox() {

        //counting SMS inbox
        //setting cursor to read SMS

        Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, new String[]{"date"}, "thread_id = " + thread_id, null, null);
        cursor.moveToFirst();
        do {
            String date = "";
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {

                //Gets COlumns

                date = cursor.getString(0);


                String newD = convertDate(date, "MM-dd-yyyy");

                //Attempts to add new person if they dont exsits, also adds body

                try {
                    person.addNewSent(new Body(newD, ""));
                } catch (Exception e) {
                    Log.d("Debug", e.getMessage() + "");
                }

            }


        } while (cursor.moveToNext());

        cursor.close();


        //counting MMS inbox
//
//        ContentResolver cr = getActivity().getContentResolver();
//        //setting cursor to read MMS
//        Cursor cursor2 = cr.query(Telephony.Mms.Sent.CONTENT_URI, // Official CONTENT_URI from docs
//                new String[]{Telephony.Mms.Inbox.DATE}, // Select body text
//                "thread_id = "+ thread_id,
//                null,
//                null); // Default sort order
//        cursor2.moveToFirst();
//
//        do {
//
//
//            //getting ID of MMS
//            String date = cursor2.getString(0);
//
//
//            String newD = convertDate(date + "000", "MM-dd-yyyy");
//
//            person.addNewSent(new Body(newD, "mms"));
//
//
//
//
//        } while (cursor2.moveToNext());
//
//        cursor2.close();


    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(colChart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            previewChart.setCurrentViewport(tempViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart because usually viewport changes
            // happens to often.
            colChart.setCurrentViewport(newViewport);
        }

    }


}



	


	

