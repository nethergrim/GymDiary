package com.nethergrim.combogymdiary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class EditingProgramAtTrainingActivity extends FragmentActivity
		implements LoaderCallbacks<Cursor> {

	private ListView lvMain;
	private DB db;
	private long traID = 0;
	private SimpleCursorAdapter scAdapter;
	private String traName;
	private String[] exercisesOld;
	private boolean ifAddingExe = false;
	private EditText etName;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		setContentView(R.layout.activity_editing_program_at_training);
		lvMain = (ListView) findViewById(R.id.lvExers);
		lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Intent in = getIntent();
		db = new DB(this);
		db.open();
		etName = (EditText) findViewById(R.id.etNewNameOfProgram);
		ifAddingExe = in.getBooleanExtra("ifAddingExe", false);
		traName = in.getStringExtra("trName");
		traID = in.getLongExtra("trID", 0);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { android.R.id.text1 };
		scAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_multiple_choice, null, from,
				to, 0);
		lvMain.setAdapter(scAdapter);
		getSupportLoaderManager().initLoader(0, null, this);
		if (ifAddingExe) {
			etName.setEnabled(false);
			etName.setText(traName);
			getActionBar().setTitle(
					getResources().getString(R.string.add_an_exercise));
		} else {
			if (traID > 0) {
				initData();
			}
		}
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
		if (!ifAddingExe) {
			setClicked(cursor);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		scAdapter.swapCursor(null);
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
			cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
			return cursor;
		}
	}

	private void initData() {
		String[] args = { "" + traID };
		Cursor c = db.getDataTrainings(null, DB.COLUMN_ID + "=?", args, null,
				null, null);
		c.moveToFirst();
		getActionBar().setTitle(
				getResources().getString(R.string.editing_program) + ":  "
						+ c.getString(1));
		etName.setText(c.getString(1));
		exercisesOld = db.convertStringToArray(c.getString(2));
	}

	private void setClicked(Cursor c) {

		if (c.moveToFirst()) {
			int i = 0;
			do {
				for (int j = 0; j < exercisesOld.length; j++) {

					if (c.getString(2).equals(exercisesOld[j])) {
						Log.d("myLogs", " c.getString(2) == " + c.getString(2)
								+ " == exercisesOld[j] == " + exercisesOld[j]);
						lvMain.setItemChecked(i, true);
					}
				}
				i++;
			} while (c.moveToNext());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editing_program_at_training, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionSaveEdited:
			if (ifAddingExe) {
				addExe();
			} else {
				editProgram();
			}
			break;
		case android.R.id.home:
			super.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addExe() {
		long[] arrIDs = lvMain.getCheckedItemIds();
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		intent.putExtra("return_array_of_exersices", arrIDs);
		finish();
	}

	private void editProgram() {
		long[] arrIDs = lvMain.getCheckedItemIds();
		Cursor cur = db.getDataExe(null, null, null, null, null, null);
		String[] args = new String[arrIDs.length];
		if (cur.moveToFirst()) {
			int i = 0;
			do {
				for (int j = 0; j < arrIDs.length; j++) {
					if (cur.getInt(0) == arrIDs[j]) {
						args[i] = cur.getString(2);
						i++;
					}
				}
			} while (cur.moveToNext());
			String newW = db.convertArrayToString(args);
			db.updateRec_Training((int) traID, 1, etName.getText().toString());
			db.updateRec_Training((int) traID, 2, newW);
		}
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();

	}

	@Override
	public void onBackPressed() {
		if (ifAddingExe == false) {
			editProgram();
		} else {
			addExe();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
