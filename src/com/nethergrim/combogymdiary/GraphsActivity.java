package com.nethergrim.combogymdiary;

import com.google.android.gms.common.data.Freezable;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class GraphsActivity extends BasicMenuActivity {

	private FrameLayout content;
	private Spinner spinner;
	private DB db;
	private Cursor exersicesCursor ;
	private String[] exeArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_graphs);
		getActionBar().setTitle(getResources().getString(R.string.statistics));
		content = (FrameLayout)findViewById(R.id.frameStatsContent);
		spinner = (Spinner)findViewById(R.id.spinner1);
		initExeCursor();
		
		// Настраиваем адаптер
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.addAll(exeArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Вызываем адаптер
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onNothingSelected(AdapterView<?> parent) {
	        }
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View view,
	                int pos, long id) {
	        	TextView tv = (TextView) view.findViewById(android.R.id.text1);
	        	String name = tv.getText().toString();
				selected(pos, id, name);
	        }
	    });
		
		
		
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
			      new GraphViewData(1, 2.0d)
			      , new GraphViewData(2, 1.5d)
			      , new GraphViewData(3, 2.5d)
			      , new GraphViewData(4, 1.0d)
			});
			 
			GraphView graphView = new LineGraphView(
			      this // context
			      , "GraphViewDemo" // heading
			);
			graphView.addSeries(exampleSeries); // data
			 
			
			content.addView(graphView);
	}

	private void selected (int pos, long id, String name){
		
	}
	
	@Override
	public void onClick(View arg0) {
		pressButton(arg0.getId());
	}
	
	private void initExeCursor(){
		db = new DB(this);
		db.open();
		exersicesCursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
		if (exersicesCursor.moveToFirst()){
			int size = exersicesCursor.getCount();
			exeArray = new String[size];
			for (int i = 0; i < size; i++) {
				exeArray[i] = exersicesCursor.getString(2);
				exersicesCursor.moveToNext();
			}
		}
	}
	
	public class GraphViewDataWeight implements GraphViewDataInterface {
	 
	    private String date;
	    private int weight;

	    public GraphViewData(String date, int weight, int reps) {
	    	this.date = date;
	        this.weight = weight;
	       
	    }

	    
	    public String getX() {
	        return this.date;
	    }

	    @Override
	    public double getY() {
	        return this.y;
	    }
	}


}
