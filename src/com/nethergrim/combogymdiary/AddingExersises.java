package com.nethergrim.combogymdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddingExersises extends BasicMenuActivity implements OnClickListener {

	Button btnCreate; 					
	EditText etName, etTimer;		
	String exeName = "", timerV = "";
	int exePosition = 0;
	long exeID = 0;
	String defaultTimer;
	Boolean editOrNot = false;	
	DB db;
	SharedPreferences sp;
	// исправил под базу версии 3
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.adding_exersise);
		getActionBar().setTitle(R.string.create_new_exercise);
		btnCreate = (Button) findViewById(R.id.btnSave);
		btnCreate.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etTimerValue);
		etTimer = (EditText) findViewById(R.id.editText2);
		
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
	}
	
	protected void onResume() {
	    defaultTimer = sp.getString("etDefault", "60" );
	    etTimer.setText(defaultTimer);
	    super.onResume();
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

	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }		
}
