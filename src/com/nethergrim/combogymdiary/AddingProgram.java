package com.nethergrim.combogymdiary;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddingProgram extends BasicMenuActivity implements
		LoaderCallbacks<Cursor> {

	private Button btnAdd;
	private EditText etName;
	private ListView lvExe;
	private DB db;
	private SimpleCursorAdapter adapter;
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		mMenuDrawer.setContentView(R.layout.adding_program);
		btnAdd = (Button) findViewById(R.id.buttonAddingProgram);
		btnAdd.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etTimerValue);
		getActionBar().setTitle(R.string.creating_program);
		lvExe = (ListView) findViewById(R.id.listView1);
		lvExe.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		db = new DB(this);
		db.open();
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { android.R.id.text1, };
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_multiple_choice, null, from,
				to, 0);
		lvExe.setAdapter(adapter);
		getSupportLoaderManager().initLoader(0, null, this);
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
			cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
			return cursor;
		}
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);
		if (id == R.id.buttonAddingProgram) {
			String prgName = etName.getText().toString();
			long[] arrIDs = lvExe.getCheckedItemIds();

			if (!prgName.isEmpty() && arrIDs.length > 0) {

				Cursor c = db.getDataExe(null, null, null, null, null, null);
				c.moveToFirst();
				String[] exersices = new String[arrIDs.length];
				int j = 0;
				do {
					if (c.getInt(0) == arrIDs[j]) {
						Log.d(LOG_TAG, "c id ==  " + c.getInt(0)
								+ " exe name == " + c.getString(2));
						exersices[j] = c.getString(2);

						j++;
					}
				} while (c.moveToNext() && j < arrIDs.length);

				db.addRec_Trainings(prgName, db.convertArrayToString(exersices));
				NavUtils.navigateUpFromSameTask(this);
			} else {
				Toast.makeText(this, R.string.input_data, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
