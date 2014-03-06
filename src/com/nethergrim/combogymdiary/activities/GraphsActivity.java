package com.nethergrim.combogymdiary.activities;

import java.util.ArrayList;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class GraphsActivity extends BasicMenuActivity {

	private FrameLayout content;
	private Spinner spinner;
	private DB db;
	private Cursor exersicesCursor;
	private Cursor dataCursor;
	private ArrayList<String> alExersices = new ArrayList<String>();
	private GraphView graphView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DB(this);
		db.open();
		mMenuDrawer.setContentView(R.layout.activity_graphs);
		getActionBar().setTitle(getResources().getString(R.string.statistics));
		content = (FrameLayout) findViewById(R.id.frameStatsContent);
		spinner = (Spinner) findViewById(R.id.spinner1);
		initExeCursor();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.addAll(alExersices);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

		graphView = new LineGraphView(this, "");
		graphView.setScalable(true);
		graphView.setScrollable(true);
		graphView.setShowLegend(true);
		graphView.getGraphViewStyle().setLegendBorder(20);
		graphView.getGraphViewStyle().setLegendSpacing(30);
		graphView.getGraphViewStyle().setLegendWidth(300);
		((LineGraphView) graphView).setDrawDataPoints(true);
		((LineGraphView) graphView).setDataPointsRadius(10f);
		content.addView(graphView);
	}

	private void selected(int pos, long id, String name) {
		graphView.removeAllSeries();
		String[] args = { name };
		dataCursor = db.getDataMain(null, DB.EXE_NAME + "=?", args, null, null,
				null);

		if (dataCursor.moveToFirst()) {
			graphView.setTitle(name);
			content.setVisibility(View.VISIBLE);

			/*******************************************************
			 * 
			 * adding weights line on graph
			 * 
			 *******************************************************/
			int i = 0;
			GraphViewData[] weightsArray = new GraphViewData[dataCursor
					.getCount()];
			do {
				weightsArray[i] = new GraphViewData(i, dataCursor.getInt(4));
				i++;
			} while (dataCursor.moveToNext());
			GraphViewSeries weightsSeries = new GraphViewSeries(getResources()
					.getString(R.string.weight), new GraphViewSeriesStyle(
					Color.rgb(153, 51, 204), 4), weightsArray);
			graphView.addSeries(weightsSeries);

			/*******************************************************
			 * 
			 * adding reps line on graph
			 * 
			 *******************************************************/

			int j = 0;
			GraphViewData[] repsArray = new GraphViewData[dataCursor.getCount()];
			dataCursor.moveToFirst();
			do {
				repsArray[j] = new GraphViewData(j, dataCursor.getInt(5));
				j++;
			} while (dataCursor.moveToNext());
			GraphViewSeries repsSeries = new GraphViewSeries(getResources()
					.getString(R.string.Reeps), new GraphViewSeriesStyle(
					Color.rgb(255, 136, 00), 4), repsArray);
			graphView.addSeries(repsSeries);

			graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
				@Override
				public String formatLabel(double value, boolean isValueX) {
					if (isValueX) {
						int pos = (int) value;
						if (pos > 1 && pos < dataCursor.getCount()) {
							dataCursor.moveToPosition(pos);
							return dataCursor.getString(3);
						} else if (pos > 0 && pos < 1) {
							dataCursor.moveToPosition(0);
							return dataCursor.getString(3);
						} else
							return null;

					} else {
						int tmp = (int) value;
						String result = String.valueOf(tmp);
						return result;
					}
				}
			});
		} else {
			content.setVisibility(View.GONE);
		}
	}

	private void initExeCursor() {
		exersicesCursor = db.getDataExe(null, null, null, null, null,
				DB.EXE_NAME);
		if (exersicesCursor.moveToFirst()) {
			do {
				alExersices.add(exersicesCursor.getString(2));
			} while (exersicesCursor.moveToNext());
		}
		exersicesCursor.close();
	}

	public class GraphViewData implements GraphViewDataInterface {

		private int date;
		private int weight;

		public GraphViewData(int _date, int _weight) {
			this.date = _date;
			this.weight = _weight;
		}

		@Override
		public double getX() {
			return this.date;
		}

		@Override
		public double getY() {
			return this.weight;
		}
	}

	protected void onDestroy() {
		db.close();
		super.onDestroy();

	}

	
	@Override
	protected void onResume() {
		super.onResume();
		activateButton(btnMenuGraphs);		
	}
	
	@Override 
	public void onPause(){
		super.onPause();
		deactivateButton(btnMenuGraphs);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View arg0) {
		pressButton(arg0.getId(), true);
	}

}
