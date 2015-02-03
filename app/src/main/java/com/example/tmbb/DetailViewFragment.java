package com.example.tmbb;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

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
    Person person;
    TextView textviewReceived;
    TextView textviewSent;
    TextView name;
    //persons List
    Map<String, Person> persons = new HashMap<String, Person>();
    ColumnChartView colChart;
    List<Column> columns = new ArrayList<Column>();
    List<AxisValue> axisValues = new ArrayList<AxisValue>();
    private ColumnChartData data;

    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get intent that was sent by activity
        String PersonName = (String) getActivity().getIntent()
                .getSerializableExtra("PERSON_ID");
        try {
            //try to open the serializable that was written to file
            FileInputStream fis = getActivity().openFileInput("persons.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            Map<String, Person> simpleClass = (Map<String, Person>) is.readObject();
            is.close();
            persons = simpleClass;
            Log.d("DEBUG", "READ1" + persons.size());
        } catch (Exception e) {

        }

        //finding selected person from personsList
        for (String address : persons.keySet()) {
            if (persons.get(address).getName().equals(PersonName))
                person = persons.get(address);
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_fragment, parent, false);

        textviewReceived = (TextView) v.findViewById(R.id.textViewReceived);
        textviewReceived.setText("" + person.received.size());
//
        textviewSent = (TextView) v.findViewById(R.id.textViewSent);
        textviewSent.setText("" + person.sent.size() + "\t");

        name = (TextView) v.findViewById(R.id.textName);
        name.setText("\t" + person.getName());


        //creating pieChart
        PieChartView pieChart = (PieChartView) v.findViewById(R.id.piechart);
        List<SliceValue> values = new ArrayList<SliceValue>();
        values.add(new SliceValue(person.sent.size(), 0xFF63CBB0));
        values.add(new SliceValue(person.received.size(), Color.parseColor("#56B7F1")));
        PieChartData dataPie = new PieChartData();
        dataPie.setValues(values);
        dataPie.setHasLabels(true);
        dataPie.setHasLabelsOutside(true);
        dataPie.setHasCenterCircle(true);
        dataPie.setCenterCircleColor(Color.parseColor("#E0F7FA"));
        pieChart.setChartRotationEnabled(false);
        pieChart.setPieChartData(dataPie);
        pieChart.setChartRotation(120, false);


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
            values.add(new SubcolumnValue(received, 0xFF63CBB0));
            values.add(new SubcolumnValue(sent, Color.parseColor("#56B7F1")));


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


}



	


	

