package com.nethergrim.combogymdiary;


import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;


public class MainActivity extends BasicMenuActivity {

	private Button btnSettings;
	private Button btnStartT;
	private Button btnExcersises;
	private Button btnWorklog;
	private Button btnCatalog;
	private Button btnMeasurements;
	private DB db;
	private Cursor cursor;
	private SharedPreferences sp;
	public static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer.setContentView(R.layout.activity_main);
        btnSettings   = (Button) findViewById(R.id.buttonSettings);
        btnStartT     = (Button) findViewById(R.id.buttonStartTraining);
        
        btnExcersises = (Button) findViewById(R.id.buttonExcersisesList);
        btnWorklog    = (Button) findViewById(R.id.btnWorklog);
        btnCatalog	  = (Button) findViewById(R.id.btnCataloginMain);
        btnMeasurements=(Button) findViewById(R.id.btnMeasurements);
        btnMeasurements.setOnClickListener(this);
        btnCatalog.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnExcersises.setOnClickListener(this);
        btnStartT.setOnClickListener(this);
        btnWorklog.setOnClickListener(this);
        ma=this;
        AdapterHelper.setContext(getApplicationContext());
        ActionBar bar = getActionBar();
        bar.setTitle(R.string.app_name); 
        db = new DB(this);
		db.open();
		cursor = db.getDataExe(null, null, null, null, null, null);
		if (cursor.getCount() < 10) {
			initTable();
		}
    }
    
    @Override
    public void onResume(){
    	sp = PreferenceManager.getDefaultSharedPreferences(this);        
        if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
        	startActivity(new Intent(this,TrainingAtProgress.class));
        	btnStartT.setBackgroundColor(getResources().getColor(R.color.holo_orange_dark));
        	btnStartT.setText(getResources().getString(R.string.continue_training));
        	
        	

        	
        } else {
        	btnStartT.setText(getResources().getString(R.string.startTrainingButtonString));
        }
        super.onResume();
    }
    
    
    
    private void initTable(){
        String[] exeLegs = getResources().getStringArray(R.array.exercisesArrayLegs);
		String[] exeChest = getResources().getStringArray(R.array.exercisesArrayChest);
		String[] exeBack = getResources().getStringArray(R.array.exercisesArrayBack);
		String[] exeShoulders = getResources().getStringArray(R.array.exercisesArrayShoulders);
		String[] exeBiceps = getResources().getStringArray(R.array.exercisesArrayBiceps);
		String[] exeTriceps = getResources().getStringArray(R.array.exercisesArrayTriceps);
		String[] exeAbs = getResources().getStringArray(R.array.exercisesArrayAbs);

		db.addRec_Trainings( getString(R.string.traLegs),db.convertArrayToString(exeLegs) );
		db.addRec_Trainings( getString(R.string.traChest), db.convertArrayToString(exeChest) );
		db.addRec_Trainings( getString(R.string.traBack),db.convertArrayToString(exeBack) );
		db.addRec_Trainings( getString(R.string.traShoulders),db.convertArrayToString(exeShoulders) );
		db.addRec_Trainings( getString(R.string.traBiceps),db.convertArrayToString(exeBiceps) );
		db.addRec_Trainings( getString(R.string.traTriceps),db.convertArrayToString(exeTriceps));
		db.addRec_Trainings( getString(R.string.traAbs),db.convertArrayToString(exeAbs));
		
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
			Intent gotoSettings = new Intent(this,SettingsActivity.class);
			startActivity(gotoSettings);
		} else if (id == R.id.buttonStartTraining) {
			if (isTrainingAtProgress) {
    			Intent start = new Intent(this,TrainingAtProgress.class);
    			String str = sPref.getString(TRAINING_NAME, "");
    			start.putExtra("trainingName", str);
    			startActivity(start);
    		}else {
    			Intent gotoStartTraining = new Intent (this,StartTrainingActivity.class);
    			startActivity(gotoStartTraining);
    		}
		} else if (id == R.id.buttonExcersisesList) {
			Intent gotoExersisesList = new Intent (this,ExersisesList.class);
			startActivity(gotoExersisesList);
		} else if (id == R.id.btnWorklog){
			Intent gotoWorklog = new Intent (this,HistoryActivity.class);
			startActivity(gotoWorklog);
		} else if (id == R.id.btnCataloginMain) {
			Intent gotoCatalog = new Intent (this,CatalogActivity.class);
			startActivity(gotoCatalog);
		} else if (id == R.id.btnMeasurements){
			Intent gotoMeasurements = new Intent (this,MeasurementsActivity.class);
			startActivity(gotoMeasurements);
		}
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();	    
	  }
	
}
