package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.yandex.metrica.Counter;

public class HistoryFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ListView lvMain;
	private DB db;
	private static final int CM_DELETE_ID = 5;
	private static final int LOADER_ID = 3;
	private SimpleCursorAdapter adapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DB(getActivity());
		db.open();
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_history, null);
		getActivity().getActionBar().setTitle(R.string.training_history);
		lvMain = (ListView) v.findViewById(R.id.lvMainHistory);
		registerForContextMenu(lvMain);
		String[] from = new String[] { DB.DATE, DB.TRA_NAME };
		int[] to = new int[] { R.id.tvDouble1, R.id.tvDouble2 };
		adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.list_with_arrow_double_textview, null, from, to, 0);
		lvMain.setAdapter(adapter);
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(LOADER_ID, null, this);
		lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				LinearLayout par = (LinearLayout) itemClicked;
				TextView t = (TextView) par.findViewById(R.id.tvDouble1);
				String date = (String) t.getText();
				TextView tra = (TextView) par.findViewById(R.id.tvDouble2);
				String traName = (String) tra.getText();
				goToDetailed(position, id, date, traName);
			}
		});
		return v;
	}

	public void goToDetailed(int position, long ID, String date, String traName) {
		Intent intent_history_detailed = new Intent(getActivity(),
				HistoryDetailedActivity.class);
		intent_history_detailed.putExtra("date", date);
		intent_history_detailed.putExtra("trName", traName);
		intent_history_detailed.putExtra("traID", ID);
		startActivity(intent_history_detailed);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(LOADER_ID).forceLoad();
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			int id = (int) acmi.id;
			String[] args = { "" + id };
			Cursor c = db.getDataMain(null, DB.COLUMN_ID + "=?", args, null,
					null, null);
			c.moveToFirst();
			String dateToDelete = c.getString(3);
			String[] argsDate = { dateToDelete };
			Cursor cur = db.getDataMain(null, DB.DATE + "=?", argsDate, null,
					null, null);
			if (cur.moveToFirst()) {
				do {
					db.delRec_Main(cur.getInt(0));
				} while (cur.moveToNext());
			}
			try {
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				adapter.notifyDataSetChanged();
				Counter.sharedInstance().reportError("", e);
			}

			((FragmentActivity) getActivity()).getSupportLoaderManager()
					.getLoader(LOADER_ID).forceLoad();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(getActivity(), db);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	static class MyCursorLoader extends CursorLoader {

		DB db;
		Cursor cursor;

		public MyCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			cursor = db.getDataMain(null, null, null, DB.DATE, null,
					DB.COLUMN_ID + " DESC");
			return cursor;
		}
	}
}
