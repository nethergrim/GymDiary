package com.nethergrim.combogymdiary;

import java.util.Locale;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startad.lib.SADView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StartTrainingActivity extends BasicMenuActivity implements
		LoaderCallbacks<Cursor> {

	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID = 2;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor_exe;
	private ListView lvMain;
	protected SADView sadView;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		mMenuDrawer.setContentView(R.layout.start_training);
		lvMain = (ListView) findViewById(R.id.lvStartTraining);
		getActionBar().setTitle(R.string.startTrainingList);
		db = new DB(this);
		db.open();
		cursor_exe = db.getDataTrainings(null, null, null, null, null, null);
		initList();

		FrameLayout fl = (FrameLayout) findViewById(R.id.frameAd);
		this.sadView = new SADView(this, APPLICAITON_ID);
		fl.addView(sadView);
		String lang = Locale.getDefault().getLanguage();
		if (lang.equals("ru")) {
			this.sadView.loadAd(SADView.LANGUAGE_RU);
		} else {
			this.sadView.loadAd(SADView.LANGUAGE_EN);
		}
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
	}

	private void initList() {
		String[] from = new String[] { DB.TRA_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item, null,
				from, to, 0);
		lvMain.setAdapter(scAdapter);
		registerForContextMenu(lvMain);
		lvMain.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goToTraining((int) id);
			}
		});
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
			cursor = db.getDataTrainings(null, null, null, null, null, null);
			return cursor;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		menu.add(1, CM_EDIT_ID, 0, R.string.edit);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getItemId() == CM_DELETE_ID) {
			LinearLayout llTmp = (LinearLayout) acmi.targetView;
			TextView tvTmp = (TextView) llTmp.findViewById(R.id.tvText);
			String traName = tvTmp.getText().toString();

			Log.d(LOG_TAG, "Going to delete " + traName);

			if (cursor_exe.moveToFirst()) {
				do {
					if (cursor_exe.getString(1).equals(traName)) {
						db.delRec_Trainings(cursor_exe.getInt(0));
						Toast.makeText(this,
								getResources().getString(R.string.deleted),
								Toast.LENGTH_SHORT).show();
						getSupportLoaderManager().getLoader(0).forceLoad();
					}
				} while (cursor_exe.moveToNext());
				return true;
			}

		} else if (item.getItemId() == CM_EDIT_ID) {
			long id = acmi.id;
			Intent intent = new Intent(this, EditingProgramAtTraining.class);
			intent.putExtra("trID", id);
			intent.putExtra("ifAddingExe", false);
			startActivityForResult(intent, 1);
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void goToTraining(int id) {
		if (cursor_exe.moveToFirst()) {
			String str = null;
			do {
				if (cursor_exe.getInt(0) == id) {
					str = cursor_exe.getString(1);
				}
			} while (cursor_exe.moveToNext());

			Intent intent_to_trainng = new Intent(this,
					TrainingAtProgress.class);
			if (!str.isEmpty())
				intent_to_trainng.putExtra("trainingName", str);
			startActivity(intent_to_trainng);
			finish();
		}
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();

		pressButton(id);

	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.itemAddNewProgramm) {
			Intent gotoAddingProgramActivity = new Intent(this,
					AddingProgram.class);
			startActivity(gotoAddingProgramActivity);

			return true;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start_training_activity, menu);
		return true;
	}
}