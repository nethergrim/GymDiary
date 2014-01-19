package com.nethergrim.combogymdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class AddingExersises extends BasicMenuActivity  {

	private Button btnCreate; 					
	private EditText etName, etTimer;		
	private String exeName = "", timerV = "";
	private int exePosition = 0;
	private long exeID = 0;
	private String defaultTimer;
	private Boolean editOrNot = false;	
	private DB db;
	private SharedPreferences sp;
	private RelativeLayout rl;
	private AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.adding_exersise);
		getActionBar().setTitle(R.string.create_new_exercise);
		btnCreate = (Button) findViewById(R.id.btnSave);
		btnCreate.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etTimerValue);
		etTimer = (EditText) findViewById(R.id.editText2);
		rl = (RelativeLayout)findViewById(R.id.rlAddingExe);
		db = new DB(this);
		db.open();

	    Intent intent = getIntent();	    
        exeName = intent.getStringExtra("exeName");        
        timerV = intent.getStringExtra("timerValue");        
        exePosition = intent.getIntExtra("exePosition", 0);
        exeID = intent.getLongExtra("exeID", 0);
        if ( exeName != null && timerV != null )  {
        	editOrNot = true;
        	}
        if (editOrNot) 
		{
			etName.setText(exeName);
			etTimer.setText(timerV);
		}
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        

        adView = new AdView(this);
        adView.setAdUnitId(MY_AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);
        rl.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
	}
	
	protected void onResume() {
	    defaultTimer = sp.getString("etDefault", "60" );
	    etTimer.setText(defaultTimer);
	    
	    super.onResume();
	    adView.resume();
	  }


		@Override
	public void onClick(View arg0) {
		String name = etName.getText().toString();
		String timer = etTimer.getText().toString();
		
		int id = arg0.getId();
		pressButton(id);
		if (id == R.id.btnSave && editOrNot == false){			
			if (!name.isEmpty() && !timer.isEmpty()){
				db.addRec_Exe(name , timer);
				etName.setText("");
				etTimer.setText("");
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				NavUtils.navigateUpFromSameTask(this);
			}
		} else if (id == R.id.btnSave && editOrNot == true)	{
			if (!name.isEmpty() && !timer.isEmpty()){
				db.updateRec_Exe((int) exeID, DB.EXE_NAME, name);
				db.updateRec_Exe((int) exeID, DB.TIMER_VALUE, timer);
				etName.setText("");
				etTimer.setText("");
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				NavUtils.navigateUpFromSameTask(this);
			}			
		}
	}

		
	@Override
	  public void onPause() {
	    adView.pause();
	    super.onPause();
	  }
		
	protected void onDestroy() {
		adView.destroy();
	    super.onDestroy();
	    db.close();
	  }		
}
