package com.nethergrim.combogymdiary;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends BasicMenuActivity implements
		LoaderCallbacks<Cursor> {

	private ListView lvMain;
	private DB db;
	private static final int CM_DELETE_ID = 1;
	private SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_history);
		getActionBar().setTitle(R.string.training_history);
		db = new DB(this);
		db.open();
		lvMain = (ListView) findViewById(R.id.lvMainHistory);
		registerForContextMenu(lvMain);
		String[] from = new String[] { DB.DATE, DB.TRA_NAME };
		int[] to = new int[] { R.id.tvDouble1, R.id.tvDouble2 };
		adapter = new SimpleCursorAdapter(this,
				R.layout.list_with_arrow_double_textview, null, from, to, 0);
		lvMain.setAdapter(adapter);
		getSupportLoaderManager().initLoader(0, null, this);
		lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				LinearLayout par = (LinearLayout) itemClicked;
				TextView t = (TextView) par.findViewById(R.id.tvDouble1);
				String date = (String) t.getText();
				TextView tra = (TextView) par.findViewById(R.id.tvDouble2);
				String traName = (String) tra.getText();
				Log.d(LOG_TAG, "Clicked pos = " + position + " id = " + id
						+ " date == " + date + " tra name == " + traName);
				goToDetailed(position, id, date, traName);
			}
		});

		AdView adView = (AdView) this.findViewById(R.id.adView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	@Override
	public void onResume() {
		getSupportLoaderManager().getLoader(0).forceLoad();
		super.onResume();
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
			int pos = acmi.position;
			int id = (int) acmi.id;
			String[] args = { "" + id };
			Cursor c = db.getDataMain(null, DB.COLUMN_ID + "=?", args, null,
					null, null);
			c.moveToFirst();
			String dateToDelete = c.getString(3);
			Log.d(LOG_TAG, "position to delete == " + pos + " id == " + id
					+ " _id == " + c.getInt(0) + " date == " + dateToDelete);
			String[] argsDate = { dateToDelete };
			Cursor cur = db.getDataMain(null, DB.DATE + "=?", argsDate, null,
					null, null);
			Log.d(LOG_TAG, "Goint to delete " + cur.getCount()
					+ " exercises from history");
			if (cur.moveToFirst()) {
				do {
					db.delRec_Main(cur.getInt(0));
				} while (cur.moveToNext());
			}
			adapter.notifyDataSetChanged();
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void goToDetailed(int position, long ID, String date, String traName) {
		Intent intent_history_detailed = new Intent(this,
				HistoryDetailedActivity.class);
		intent_history_detailed.putExtra("date", date);
		intent_history_detailed.putExtra("trName", traName);
		intent_history_detailed.putExtra("traID", ID);
		startActivity(intent_history_detailed);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
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
			cursor = db.getDataMain(null, null, null, DB.DATE, null, DB.COLUMN_ID);
			return cursor;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);
	}
}
