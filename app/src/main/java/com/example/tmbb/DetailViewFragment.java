package com.example.tmbb;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.StackedBarModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;


public class DetailViewFragment extends Fragment{
	Person person;
	ArrayList<Person> persons;
	TextView textviewReceived;
	TextView textviewSent;
	TextView name;



	

	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String PersonName = (String)getActivity().getIntent()
				.getSerializableExtra("PERSON_ID");
		persons = (ArrayList<Person>)getActivity().getIntent()
				.getSerializableExtra("PERSON_ARRAY");
		
		for( Person dummy : persons){
			if(dummy.getName() != null){
			if(dummy.getName().equals(PersonName)){
				person = dummy;
			}}
		}
		

	}
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.detail_fragment, parent, false);
	
		textviewReceived = (TextView) v.findViewById(R.id.textViewReceived);
		textviewReceived.setText("" + person.received.size());
//
		textviewSent = (TextView) v.findViewById(R.id.textViewSent);
		textviewSent.setText("" + person.sent.size()+ "\t");
		
		name = (TextView) v.findViewById(R.id.textName);
		name.setText("\t" + person.getName());

        PieChart mPieChart = (PieChart) v.findViewById(R.id.piechart);

        mPieChart.addPieSlice(new PieModel("Dailton Rabelo", person.sent.size(), 0xFF63CBB0));
        mPieChart.addPieSlice(new PieModel(person.getName(), person.received.size(), Color.parseColor("#56B7F1")));


        mPieChart.startAnimation();


        StackedBarChart mStackedBarChart = mkBarGraph(v);
        mStackedBarChart.startAnimation();


        return v;
	}
	
	
	public StackedBarChart mkBarGraph(View v){



        DateTime now = DateTime.now();
        DateTime start = now;
        DateTime stop = now.minusDays(6);
        DateTime inter = start;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTimeFormatter format = DateTimeFormat.forPattern("MM-dd-yyyy");
        DateTimeFormatter format2 = DateTimeFormat.forPattern("E");

        StackedBarChart mStackedBarChart = (StackedBarChart) v.findViewById(R.id.stackedbarchart);
        int received = 0;
        int sent = 0;

// Loop through each day in the span
        while (!fmt.print(stop).equals(fmt.print(now))) {
            for(Body p : person.received){
                if(format.print(stop).equals(p.date)) {
                    received++;
                }
            }

            for(Body p : person.sent){
                if(format.print(stop).equals(p.date)) {
                    sent++;
                }
            }


            StackedBarModel s1 = new StackedBarModel(format2.print(stop));
            Long rec = new Long(received);
            Long sen = new Long(sent);

            s1.addBar(new BarModel(rec, 0xFF56B7F1));
            s1.addBar(new BarModel(sen, 0xFF63CBB0));

            mStackedBarChart.addBar(s1);




            stop = stop.plusDays(1);
        }


        return mStackedBarChart;




    }



    }
	
	


	

