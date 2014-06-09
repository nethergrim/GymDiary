package com.nethergrim.combogymdiary;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Statistics {

	private DB db;
	private Context context;
	private static final String LOG_TAG = "myLogs";

	public Statistics(Context _context) {
		this.context = _context;
		this.db = new DB(context);
		db.open();
		Log.d(LOG_TAG, "Statistics constructor");
	}

	public String getMainExercise() {
		String result = context.getResources().getString(R.string.none);
		Cursor exercises = db.getDataMain(DB.EXE_NAME);
		ArrayList<String> alExercises = new ArrayList<String>();
		if (exercises.moveToFirst()) {
			do {
				alExercises.add(exercises.getString(2));
			} while (exercises.moveToNext());
		}

		Cursor allCursor = db.getDataMain(null, null, null, null, null,
				DB.EXE_NAME);

		return result;
	}

}
