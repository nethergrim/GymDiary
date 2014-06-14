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

    private final static String DATABASE_FILLED = "database_filled";
    public static boolean TEST = false;
    private SharedPreferences sp;
    private DB db;

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
            InitTask task = new InitTask();
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

        for (String exeLeg : exeLegs) db.addRecExe(exeLeg, "90");
        for (String anExeChest : exeChest) db.addRecExe(anExeChest, "60");
        for (String exeBicep : exeBiceps) db.addRecExe(exeBicep, "60");
        for (String exeTricep : exeTriceps) db.addRecExe(exeTricep, "60");
        for (String anExeBack : exeBack) db.addRecExe(anExeBack, "60");
        for (String exeShoulder : exeShoulders) db.addRecExe(exeShoulder, "60");
        for (String exeAb : exeAbs) db.addRecExe(exeAb, "60");
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
