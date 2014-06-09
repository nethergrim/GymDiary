package com.nethergrim.combogymdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

public class StartActivity extends Activity {

	public static boolean TEST = false;
	private SharedPreferences sp;
	private DB db;
	private final static String DATABASE_FILLED = "database_filled";
	private InitTask task;

	public static boolean getTest() {
		return TEST;
	}

	public static void setTest(boolean ifTest) {
		TEST = ifTest;
	}

	private void goNext() {
		Intent gotoStartTraining = new Intent(this, BasicMenuActivityNew.class);
		startActivity(gotoStartTraining);
	}

	private void initUi() {
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		db = new DB(this);
		db.open();
		Cursor tmp = db.getDataExe(null, null, null, null, null, null);
		if (tmp.getCount() < 3) {
			sp.edit().putBoolean(DATABASE_FILLED, false).apply();
		} else {
			sp.edit().putBoolean(DATABASE_FILLED, true).apply();
		}
		tmp.close();
	}

	@Override
	public void onResume() {
		super.onResume();

		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		initUi();
		if (!sp.getBoolean(DATABASE_FILLED, false)) {
			task = new InitTask();
			task.execute();
		} else {
			goNext();
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	private void initTable() {
		String[] exeLegs = getResources().getStringArray(
				R.array.exercisesArrayLegs);
		String[] exeChest = getResources().getStringArray(
				R.array.exercisesArrayChest);
		String[] exeBack = getResources().getStringArray(
				R.array.exercisesArrayBack);
		String[] exeShoulders = getResources().getStringArray(
				R.array.exercisesArrayShoulders);
		String[] exeBiceps = getResources().getStringArray(
				R.array.exercisesArrayBiceps);
		String[] exeTriceps = getResources().getStringArray(
				R.array.exercisesArrayTriceps);
		String[] exeAbs = getResources().getStringArray(
				R.array.exercisesArrayAbs);

		db.addRecTrainings(getString(R.string.traLegs),
				db.convertArrayToString(exeLegs));
		db.addRecTrainings(getString(R.string.traChest),
				db.convertArrayToString(exeChest));
		db.addRecTrainings(getString(R.string.traBack),
				db.convertArrayToString(exeBack));
		db.addRecTrainings(getString(R.string.traShoulders),
				db.convertArrayToString(exeShoulders));
		db.addRecTrainings(getString(R.string.traBiceps),
				db.convertArrayToString(exeBiceps));
		db.addRecTrainings(getString(R.string.traTriceps),
				db.convertArrayToString(exeTriceps));
		db.addRecTrainings(getString(R.string.traAbs),
				db.convertArrayToString(exeAbs));

		for (int i = 0; i < exeLegs.length; i++)
			db.addRecExe(exeLegs[i], "90");
		for (int i = 0; i < exeChest.length; i++)
			db.addRecExe(exeChest[i], "60");
		for (int i = 0; i < exeBiceps.length; i++)
			db.addRecExe(exeBiceps[i], "60");
		for (int i = 0; i < exeTriceps.length; i++)
			db.addRecExe(exeTriceps[i], "60");
		for (int i = 0; i < exeBack.length; i++)
			db.addRecExe(exeBack[i], "60");
		for (int i = 0; i < exeShoulders.length; i++)
			db.addRecExe(exeShoulders[i], "60");
		for (int i = 0; i < exeAbs.length; i++)
			db.addRecExe(exeAbs[i], "60");
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();

	}

	class InitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			sp.edit().putBoolean(DATABASE_FILLED, true).apply();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				initTable();
			} catch (Exception e) {
				Counter.sharedInstance().reportError("", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			goNext();
			finish();
		}
	}
}
