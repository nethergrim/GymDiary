package com.nethergrim.combogymdiary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends Activity implements OnClickListener {

	Button btnSettings;
	Button btnStartT;
	Button btnExcersises;
	Button btnWorklog;
	public static MainActivity ma;
	DB db;
	Cursor cursor;
	final String LOG_TAG = "myLogs";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSettings   = (Button) findViewById(R.id.buttonSettings);
        btnStartT     = (Button) findViewById(R.id.buttonStartTraining);
        btnExcersises = (Button) findViewById(R.id.buttonExcersisesList);
        btnWorklog    = (Button) findViewById(R.id.btnWorklog);
        btnSettings.setOnClickListener(this);
        btnExcersises.setOnClickListener(this);
        btnStartT.setOnClickListener(this);
        btnWorklog.setOnClickListener(this);
        ma=this;
        ActionBar bar = getActionBar();
        bar.setTitle(R.string.app_name); 
        db = new DB(this);
		db.open();
		cursor = db.getAllData_Exe();
		if (cursor.getCount() < 60) {
			initTable();
		}
    }

    private void initTable(){
		Log.d(LOG_TAG, "cursor getcount < 60 ");
        String[] exeLegs = getResources().getStringArray(R.array.exercisesArrayLegs);
		String[] exeChest = getResources().getStringArray(R.array.exercisesArrayChest);
		String[] exeBack = getResources().getStringArray(R.array.exercisesArrayBack);
		String[] exeShoulders = getResources().getStringArray(R.array.exercisesArrayShoulders);
		String[] exeArms = getResources().getStringArray(R.array.exercisesArrayArms);
		String[] exeAbs = getResources().getStringArray(R.array.exercisesArrayAbs);
		for (int i = 0; i < exeLegs.length; i++) 
			db.addRec_Exe(getString(R.string.traLegs), exeLegs[i], "90");
		for (int i = 0; i < exeChest.length; i++) 
			db.addRec_Exe(getString(R.string.traChest), exeChest[i], "60");
		for (int i = 0; i < exeArms.length; i++) 
			db.addRec_Exe(getString(R.string.traArms), exeArms[i], "60");
		for (int i = 0; i < exeBack.length; i++) 
			db.addRec_Exe(getString(R.string.traBack), exeBack[i], "60");
		for (int i = 0; i < exeShoulders.length; i++) 
			db.addRec_Exe(getString(R.string.traShoulders), exeShoulders[i], "60");
		for (int i = 0; i < exeAbs.length; i++) 
			db.addRec_Exe(getString(R.string.traAbs), exeAbs[i], "60");
		Log.d(LOG_TAG, "cursor getcount = " + cursor.getCount());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) 
    {
    	switch (item.getItemId()) 
    	{
    	case android.R.id.home:    		
    		break;
    	}
		return false;    	
    }    
        
	@Override
	public void onClick(View arg0) {
	    int id = arg0.getId();
		if (id == R.id.buttonSettings) {
			Intent gotoSettings = new Intent(this,SettingsActivity.class);
			startActivity(gotoSettings);
		} else if (id == R.id.buttonStartTraining) {
			Intent gotoStartTraining = new Intent (this,StartTrainingActivity.class);
			startActivity(gotoStartTraining);
		} else if (id == R.id.buttonExcersisesList) {
			Intent gotoExersisesList = new Intent (this,ExersisesList.class);
			startActivity(gotoExersisesList);
		} else if (id == R.id.btnWorklog){
			Intent gotoWorklog = new Intent (this,HistoryActivity.class);
			startActivity(gotoWorklog);
		}
	}
	
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();	    
	  }
	
}
