package com.nethergrim.combogymdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends BasicMenuActivity {

	private Button btnSettings;
	private Button btnStartT;
	private Button btnExcersises;
	private Button btnWorklog;
	private Button btnCatalog;
	private Button btnMeasurements;
	private SharedPreferences sp;
	private DB db;
	private Cursor cursor;
	private ProgressBar pb;
	private InitTask task;
	public static MainActivity ma;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initUi();
	}

	private void initUi() {
		mMenuDrawer.setContentView(R.layout.activity_main);
		getActionBar().setTitle(R.string.app_name);
		btnSettings = (Button) findViewById(R.id.buttonSettings);
		btnStartT = (Button) findViewById(R.id.buttonStartTraining);
		btnExcersises = (Button) findViewById(R.id.buttonExcersisesList);
		btnWorklog = (Button) findViewById(R.id.btnWorklog);
		btnCatalog = (Button) findViewById(R.id.btnCataloginMain);
		btnMeasurements = (Button) findViewById(R.id.btnMeasurementsS);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.GONE);
		btnMeasurements.setOnClickListener(this);
		btnCatalog.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		btnExcersises.setOnClickListener(this);
		btnStartT.setOnClickListener(this);
		btnWorklog.setOnClickListener(this);
		db = new DB(this);
		db.open();
		cursor = db.getDataExe(null, null, null, null, null, null);
		ma = this;

	}

	@Override
	public void onResume() {
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
			Intent intent_to_trainng = new Intent(this,
					TrainingAtProgress.class);
			startActivity(intent_to_trainng);
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();

		if (cursor.getCount() < 10) {
			pb.setVisibility(View.VISIBLE);
			task = new InitTask();
			task.execute();
		}
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

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);
		if (id == R.id.buttonSettings) {
			Intent gotoSettings = new Intent(this, SettingsActivity.class);
			startActivity(gotoSettings);
		} else if (id == R.id.buttonStartTraining) {
			if (isTrainingAtProgress) {
				Intent start = new Intent(this, TrainingAtProgress.class);
				String str = sPref.getString(TRAINING_NAME, "");
				start.putExtra("trainingName", str);
				startActivity(start);
			} else {
				Intent gotoStartTraining = new Intent(this,
						StartTrainingActivity.class);
				startActivity(gotoStartTraining);
			}
		} else if (id == R.id.buttonExcersisesList) {
			Intent gotoExersisesList = new Intent(this, ExersisesList.class);
			startActivity(gotoExersisesList);
		} else if (id == R.id.btnWorklog) {
			Intent gotoWorklog = new Intent(this, HistoryActivity.class);
			startActivity(gotoWorklog);
		} else if (id == R.id.btnCataloginMain) {
			Intent gotoCatalog = new Intent(this, CatalogActivity.class);
			startActivity(gotoCatalog);
		} else if (id == R.id.btnMeasurementsS) {
			Intent gotoMeasurements = new Intent(this,
					MeasurementsActivity.class);
			startActivity(gotoMeasurements);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	class InitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			initTable();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pb.setVisibility(View.GONE);
		}
	}
}
