package com.nethergrim.combogymdiary.fragments;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.AddingMeasurementActivity;
import com.nethergrim.combogymdiary.activities.MeasurementsDetailedActivity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MeasurementsFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ListView lvMeasurements;
	private DB db;
	private static final int LOADER_ID = 4;
	private SimpleCursorAdapter scAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_measurements, null);
		getActivity().getActionBar().setTitle(
				getResources().getString(R.string.measurements));
		lvMeasurements = (ListView) v.findViewById(R.id.lvMeasurements);

		String[] from = new String[] { DB.DATE };
		int[] to = new int[] { R.id.tvCatName };
		scAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.list_with_arrow, null, from, to, 0);
		lvMeasurements.setAdapter(scAdapter);
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(LOADER_ID, null, this);
		lvMeasurements
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View itemClicked, int position, long id) {
						LinearLayout par = (LinearLayout) itemClicked;
						TextView t = (TextView) par
								.findViewById(R.id.tvCatName);

						String date = (String) t.getText();
						gotoDetailed(position, id, date);

					}
				});

		return v;
	}

	private void gotoDetailed(int position, long id, String date) {
		Intent gotoDetailed = new Intent(getActivity(),
				MeasurementsDetailedActivity.class);
		gotoDetailed.putExtra("clicked_position_of_measurements", position);
		gotoDetailed.putExtra("clicked_id", id);
		gotoDetailed.putExtra("date", date);
		startActivity(gotoDetailed);
	}

	@Override
	public void onResume() {
		super.onResume();
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(LOADER_ID).forceLoad();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(getActivity(), db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	static class MyCursorLoader extends CursorLoader {

		private DB db;
		private Cursor cursor;

		public MyCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			cursor = db.getDataMeasures(null, null, null, DB.DATE, null,
					DB.DATE);
			return cursor;
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.clear();
		inflater.inflate(R.menu.measurements, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add_measurements) {
			Intent intent = new Intent(getActivity(),
					AddingMeasurementActivity.class);
			startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

}
