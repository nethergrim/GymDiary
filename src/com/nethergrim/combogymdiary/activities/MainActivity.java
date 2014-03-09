package com.nethergrim.combogymdiary.activities;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

	private SharedPreferences sp;
	private DB db;
	private final static String TRAINING_AT_PROGRESS = "training_at_progress";
	private final static String DATABASE_FILLED = "database_filled";
	private InitTask task;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		getActionBar().setDisplayUseLogoEnabled(false);
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
		if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
			Intent intent_to_trainng = new Intent(this,
					TrainingAtProgress.class);
			startActivity(intent_to_trainng);
			finish();
			return;
		}
		if (!sp.getBoolean(DATABASE_FILLED, false)) {
			task = new InitTask();
			task.execute();
		} else {
			Intent gotoStartTraining = new Intent(getApplicationContext(),
					StartTrainingActivity.class);
			startActivity(gotoStartTraining);
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
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

		db.addRec_Trainings(getString(R.string.traLegs),
				db.convertArrayToString(exeLegs));
		db.addRec_Trainings(getString(R.string.traChest),
				db.convertArrayToString(exeChest));
		db.addRec_Trainings(getString(R.string.traBack),
				db.convertArrayToString(exeBack));
		db.addRec_Trainings(getString(R.string.traShoulders),
				db.convertArrayToString(exeShoulders));
		db.addRec_Trainings(getString(R.string.traBiceps),
				db.convertArrayToString(exeBiceps));
		db.addRec_Trainings(getString(R.string.traTriceps),
				db.convertArrayToString(exeTriceps));
		db.addRec_Trainings(getString(R.string.traAbs),
				db.convertArrayToString(exeAbs));

		for (int i = 0; i < exeLegs.length; i++)
			db.addRec_Exe(exeLegs[i], "90");
		for (int i = 0; i < exeChest.length; i++)
			db.addRec_Exe(exeChest[i], "60");
		for (int i = 0; i < exeBiceps.length; i++)
			db.addRec_Exe(exeBiceps[i], "60");
		for (int i = 0; i < exeTriceps.length; i++)
			db.addRec_Exe(exeTriceps[i], "60");
		for (int i = 0; i < exeBack.length; i++)
			db.addRec_Exe(exeBack[i], "60");
		for (int i = 0; i < exeShoulders.length; i++)
			db.addRec_Exe(exeShoulders[i], "60");
		for (int i = 0; i < exeAbs.length; i++)
			db.addRec_Exe(exeAbs[i], "60");
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
				Counter.sharedInstance()
						.reportError("error in initTable();", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Intent gotoStartTraining = new Intent(getApplicationContext(),
					StartTrainingActivity.class);
			startActivity(gotoStartTraining);
			finish();
		}
	}
}
