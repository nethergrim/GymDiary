package com.example.combogymdiary;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrainingAtProgress extends Activity  implements OnClickListener, OnCheckedChangeListener{
	
	Button btnSave;	
	ToggleButton tglTimerOn;
	Boolean tglChecked = true,turnOff = true;
	EditText etWeight,etReps,etTimer;
	ListView lvMain;
	DB db;
	Cursor cursor;	
	final String LOG_TAG = "myLogs";
	ArrayAdapter<String> adapter;
	String[] trNamesData = {};
	int[] setsPerExercises = null;
	String traName = "", exeName = "",date = "";
	SharedPreferences sp;
	int checkedPosition = 0,set = 0,oldReps = 0,oldWeight = 0,timerValue = 0;
	DialogFragment dlg1;
	TextView tvPrevWeight,tvPrevReps,tvPrevLeft1,tvPrevLeft2;
	ProgressDialog pd;
	Handler h;
	
	
	@SuppressLint("SimpleDateFormat")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB(this);
		db.open();
        setContentView(R.layout.training_at_progress);
        ActionBar bar = getActionBar();
        Intent intent = getIntent();
        traName = intent.getStringExtra("trainingName");
        bar.setTitle(traName);   
        String[] strArrExtra = {traName};
        String[] strArrCol = {DB.COLUMN_ID,DB.EXE_NAME};            
        tvPrevWeight = (TextView)findViewById(R.id.tvPrevWeight);
        tvPrevReps = (TextView)findViewById(R.id.tvPrevReps);
        tvPrevLeft1 = (TextView)findViewById(R.id.tvPrevLeft1);
        tvPrevLeft2 = (TextView)findViewById(R.id.tvPrevLeft2);        
        tglTimerOn = (ToggleButton) findViewById(R.id.tglTurnOff);
        tglTimerOn.setOnCheckedChangeListener(this); 
        etTimer = (EditText) findViewById(R.id.etTimerValueAtTraining);
        etTimer.setOnClickListener(this);       
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);        
        etWeight = (EditText) findViewById(R.id.editWeight);
        etReps = (EditText) findViewById(R.id.editReps);  
        etWeight.setOnClickListener(this);
        etReps.setOnClickListener(this);
        lvMain = (ListView)findViewById(R.id.lvSets);
        lvMain.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        Cursor cur_exe = db.getDataExe(strArrCol, DB.TRA_NAME + "=?", strArrExtra, (String)null, (String)null, (String)null);
        int size = cur_exe.getCount();
        trNamesData = new String[size];
        setsPerExercises = new int[size];        
        for (int i = 0; i < size; i++){
        	setsPerExercises[i] = 0;
        }
        if (cur_exe.moveToFirst()){
        	int i = 0;
        	do {
        		trNamesData[i] = cur_exe.getString(1);        		
        		i++;
              } while (cur_exe.moveToNext());
        }        
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, trNamesData);
        lvMain.setAdapter(adapter);        
        lvMain.setItemChecked(0, true); 
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));
        tglTimerOn.setChecked(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);        
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View itemClicked, int position,long id) {
        		Log.d(LOG_TAG, "Item Clicked position: "+position);
        		checkedPosition = position;
        		exeName = trNamesData[checkedPosition];
        		set = setsPerExercises[checkedPosition];
        		String tValue = db.getTimerValueByExerciseName(exeName);
        		etTimer.setText(tValue);      		
        		timerValue = Integer.parseInt(db.getTimerValueByExerciseName(exeName));
                Log.d(LOG_TAG, "Progressbar MAX set to: "+timerValue);
        		oldReps = db.getLastReps(exeName, set);
        		oldWeight = db.getLastWeight(exeName, set);
        		if ( oldReps>0 && oldWeight>0 ){
        			String tmp1 = (""+oldReps);
        			String tmp2 = (""+oldWeight);
        			tvPrevReps.setHint(tmp1);
        			tvPrevWeight.setHint(tmp2);
        			tvPrevLeft1.setHint(R.string.prev);
        			tvPrevLeft2.setHint(R.string.prev);       			
        			        			
        		}else {
        			tvPrevReps.setHint("");
        			tvPrevWeight.setHint("");
        			tvPrevLeft1.setHint("");
        			tvPrevLeft2.setHint("");        			
        			}
        		}
        	});        
		exeName = trNamesData[checkedPosition];
		set = setsPerExercises[checkedPosition];
		String tValue = db.getTimerValueByExerciseName(exeName);
		etTimer.setText(tValue);
		oldReps = db.getLastReps(exeName, set);
		oldWeight = db.getLastWeight(exeName, set);
		if ( oldReps>0 && oldWeight>0 ){
			String tmp1 = (""+oldReps);
			String tmp2 = (""+oldWeight);
			tvPrevReps.setHint(tmp1);
			tvPrevWeight.setHint(tmp2);
			tvPrevLeft1.setHint(R.string.prev);
			tvPrevLeft2.setHint(R.string.prev);
		}else {
			tvPrevReps.setHint("");
			tvPrevWeight.setHint("");
			tvPrevLeft1.setHint("");
			tvPrevLeft2.setHint("");        			
			}
        dlg1 = new Dialog1();
        dlg1.setCancelable(false);
        etTimer.setText( db.getTimerValueByExerciseName(trNamesData[0]) );        
        timerValue = Integer.parseInt(db.getTimerValueByExerciseName(exeName));
        }
  
	protected void onResume() {
	    turnOff = sp.getBoolean("turnoff", false);
	    Log.d(LOG_TAG, "turnOFF = "+turnOff);
	    exeName = trNamesData[lvMain.getCheckedItemPosition()];
	    super.onResume();
	  }
	
	private void goDialogProgress () {
      pd = new ProgressDialog(this);
      pd.setTitle(R.string.resting);
      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      pd.setMax(timerValue);
      pd.setIndeterminate(true);
      pd.show();
      h = new Handler() {
        public void handleMessage(Message msg) {
          pd.setIndeterminate(false);
          if (pd.getProgress() < pd.getMax()) {
            pd.incrementProgressBy(1);
            h.sendEmptyMessageDelayed(0, 1000);
          } else {
            pd.dismiss();
          }
        }
      };
      h.sendEmptyMessageDelayed(0, 100);	    
	}
	
	
	@Override
    public boolean onOptionsItemSelected (MenuItem item){
    	int itemId = item.getItemId();
    	if (itemId == R.id.itemExit){
			dlg1.show(getFragmentManager(), "dlg1");			
		}
		return false;    	
    }
	
	@Override
	public void onCheckedChanged(CompoundButton tglTimerOn, boolean isChecked) {
		if (isChecked){
			tglChecked = true;
			btnSave.setText(R.string.save_and_rest);
		}
		else{
			tglChecked = false;
			btnSave.setText(R.string.save);			
		}			
	}	
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		Log.d(LOG_TAG, "checked: " + trNamesData[lvMain.getCheckedItemPosition()] );
		if (id == R.id.btnSave) {
			String weight = etWeight.getText().toString();
			String reps = etReps.getText().toString();			
			if (!weight.isEmpty() && !reps.isEmpty()){	
				int wei = Integer.parseInt(weight);
				int rep_s = Integer.parseInt(reps);
				String t = etTimer.getText().toString();
				timerValue = Integer.parseInt(t);
    			etWeight.setText("");
    			etReps.setText("");    			
    			exeName = trNamesData[checkedPosition];  			
    			set = ++setsPerExercises[checkedPosition];    			
    			db.addRec_Main(traName, exeName, t, date, wei, rep_s, set);	
    			Toast.makeText(this,R.string.saved, Toast.LENGTH_SHORT).show();    			
    			oldReps = db.getLastReps(exeName, set);
    			oldWeight = db.getLastWeight(exeName, set);
    			if ( oldReps>0 && oldWeight>0 ){
    				String tmp1 = (""+oldReps);
    				String tmp2 = (""+oldWeight);
    				tvPrevReps.setHint(tmp1);
    				tvPrevWeight.setHint(tmp2);
    				tvPrevLeft1.setHint(R.string.prev);
    				tvPrevLeft2.setHint(R.string.prev);
    			}else {
    				tvPrevReps.setHint("");
    				tvPrevWeight.setHint("");
    				tvPrevLeft1.setHint("");
    				tvPrevLeft2.setHint("");        			
    				}
    			if (tglChecked) {
    				goDialogProgress();
    			}
    		}
		}
	}	
	
	
	@Override
	public void onBackPressed() {
		dlg1.show(getFragmentManager(), "dlg1");
	}
	
	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
	  }
}

