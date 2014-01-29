package com.nethergrim.combogymdiary;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExersisesList extends BasicMenuActivity implements
		LoaderCallbacks<Cursor> {

	private ListView lvExersices_list;
	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID = 2;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor_exe;
	private Button btnCreate;
	private SharedPreferences sp;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		mMenuDrawer.setContentView(R.layout.exersises_list);
		btnCreate = (Button) findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(this);
		getActionBar().setTitle(R.string.excersisiesListButtonString);
		lvExersices_list = (ListView) findViewById(R.id.listView11);
		db = new DB(this);
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		String[] cols = { DB.COLUMN_ID, DB.EXE_NAME, DB.TIMER_VALUE };
		cursor_exe = db.getDataExe(cols, null, null, null, null, DB.EXE_NAME);
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { R.id.tvText, };

		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item2, null,
				from, to, 0);
		lvExersices_list.setAdapter(scAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
		registerForContextMenu(lvExersices_list);
		lvExersices_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(LOG_TAG, "In ExercisesList itemClick: position = "
						+ position + ", id = " + id);
				goToEditExe(position, id);
			}
		});

		AdView adView = (AdView) this.findViewById(R.id.adView5);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		getSupportLoaderManager().getLoader(0).forceLoad();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();

	}

	@Override
	protected void onResume() {
		getSupportLoaderManager().getLoader(0).forceLoad();
		super.onResume();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
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
			String[] cols = { DB.COLUMN_ID, DB.EXE_NAME, DB.TIMER_VALUE };
			cursor = db.getDataExe(cols, null, null, null, null, DB.EXE_NAME);
			return cursor;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		menu.add(0, CM_EDIT_ID, 0, R.string.edit);
	}

	public void goToEditExe(int position, long ID) {
		if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
			Toast.makeText(this, R.string.error_editing_exe, Toast.LENGTH_SHORT)
					.show();
		} else {
			cursor_exe.moveToFirst();
			while (cursor_exe.getPosition() < position) {
				cursor_exe.moveToNext();
			}
			String name = cursor_exe.getString(1);
			Toast.makeText(this, "Editing: " + name, Toast.LENGTH_SHORT).show();
			String timV = cursor_exe.getString(2);
			Intent intent_exe_edit = new Intent(this, AddingExersises.class);
			intent_exe_edit.putExtra("exeName", name);
			intent_exe_edit.putExtra("timerValue", timV);
			intent_exe_edit.putExtra("exePosition", position);
			intent_exe_edit.putExtra("exeID", ID);
			startActivity(intent_exe_edit);
		}
	}


	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {

			TextView tvTmp = (TextView) acmi.targetView;
			String exeName = tvTmp.getText().toString();

			Log.d(LOG_TAG,
					"training in progress == "
							+ sp.getBoolean(TRAINING_AT_PROGRESS, false));
			if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
				Toast.makeText(this, R.string.error_deleting_exe,
						Toast.LENGTH_SHORT).show();
			} else {
				db.delRec_Exe(acmi.id);
				db.deleteExersiceByName(exeName);
				Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT)
						.show();
				getSupportLoaderManager().getLoader(0).forceLoad();
			}

			return true;
		} else if (item.getItemId() == CM_EDIT_ID) {
			if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
				Toast.makeText(this, R.string.error_editing_exe,
						Toast.LENGTH_SHORT).show();
			} else {
				goToEditExe(acmi.position, acmi.id);
			}
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		}
		getSupportLoaderManager().getLoader(0).forceLoad();
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
		case R.id.btnCreate:
			Intent gotoAddingExersisesActivity = new Intent(this,
					AddingExersises.class);
			startActivity(gotoAddingExersisesActivity);
			break;
		default:
			pressButton(id);
			break;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
