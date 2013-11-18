package com.example.combogymdiary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	static MainActivity ma;
	
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
			Intent gotoWorklog = new Intent (this,TrainingListActivity.class);
			startActivity(gotoWorklog);
		}
	}
}
