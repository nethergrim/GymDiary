package com.nethergrim.combogymdiary.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class StatisticsWeightsFragment extends android.support.v4.app.Fragment
		implements LoaderCallbacks<Cursor> {

	private FrameLayout content;
	private Spinner spinnerExercises;
	private SimpleCursorAdapter adapterExercise;
	private DB db;
	private Cursor dataCursor;
	private GraphView graphView;
	private static final int LOADER_EXE_ID = 7;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_statistics, null);

		getActivity().getActionBar().setTitle(
				getResources().getString(R.string.statistics));
		content = (FrameLayout) v.findViewById(R.id.frameStatsContent);
		spinnerExercises = (Spinner) v.findViewById(R.id.spinnerExercises);

		String[] fromExe = new String[] { DB.EXE_NAME };
		int[] to = new int[] { android.R.id.text1 };
		adapterExercise = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_spinner_item, null, fromExe, to, 0);
		adapterExercise
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerExercises.setAdapter(adapterExercise);

		graphView = new LineGraphView(getActivity(), "");
		graphView.setScalable(true);
		graphView.setScrollable(true);
		graphView.setShowLegend(true);
		graphView.getGraphViewStyle().setLegendBorder(20);
		graphView.getGraphViewStyle().setLegendSpacing(30);
		graphView.getGraphViewStyle().setLegendWidth(300);

		((LineGraphView) graphView).setDrawDataPoints(true);
		((LineGraphView) graphView).setDataPointsRadius(10f);
		content.addView(graphView);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(LOADER_EXE_ID).forceLoad();
		spinnerExercises
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						TextView tv = (TextView) view
								.findViewById(android.R.id.text1);
						String name = tv.getText().toString();
						selected(pos, id, name);
					}
				});
	}

	public void onStart() {
		super.onStart();
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(LOADER_EXE_ID, null, this);
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
						} else if (pos < 1) {
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(getActivity(), db, id);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapterExercise.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapterExercise.swapCursor(null);
	}

	static class MyCursorLoader extends CursorLoader {
		DB db;
		Cursor cursor;
		int ID;

		public MyCursorLoader(Context context, DB db, int id) {
			super(context);
			this.db = db;
			this.ID = id;
		}

		@Override
		public Cursor loadInBackground() {
			if (ID == LOADER_EXE_ID) {
				cursor = db.getDataExe(null, null, null, null, null,
						DB.EXE_NAME);
			}
			return cursor;
		}
	}

}
