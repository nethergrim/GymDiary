package com.nethergrim.combogymdiary;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SettingsActivity extends PreferenceActivity  implements OnClickListener{
	
	SharedPreferences sPref;
	EditText etTimerValue;
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.settingsButtonString);

    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) 
    {
    	int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			Intent gotoMain = new Intent(this,MainActivity.class);
			startActivity(gotoMain);
		}
		return false;    	
    }
    
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
