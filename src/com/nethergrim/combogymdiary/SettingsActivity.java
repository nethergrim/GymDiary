package com.nethergrim.combogymdiary;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SettingsActivity extends PreferenceActivity  implements OnClickListener{
	final String LOG_TAG = "myLogs";
	SharedPreferences sPref;
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4,btnMenuCatalog;
	public static SettingsActivity sa;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        
        ActionBar bar = getActionBar();
        bar.setTitle(R.string.settingsButtonString);
        setupSlidingMenu();
        
        
        Preference btnBackup = (Preference)findPreference("btnBackup");
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) { 
                        	Backuper backUP = new Backuper();
                        	boolean yes = backUP.backupToSd();
                        	if (yes)
                        		Toast.makeText(getApplicationContext(),getResources().getString(R.string.backuped), Toast.LENGTH_SHORT).show();
                        	else
                        		Toast.makeText(getApplicationContext(),getResources().getString(R.string.backup_error), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
        
        Preference btnRestore = (Preference)findPreference("btnRestore");
        btnRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) { 
                        	Backuper backUP = new Backuper();
                        	boolean yes = backUP.restoreBackup();
                        	if (yes)
                        		Toast.makeText(getApplicationContext(),getResources().getString(R.string.restored), Toast.LENGTH_SHORT).show();
                        	else
                        		Toast.makeText(getApplicationContext(),getResources().getString(R.string.restore_error), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
    }
    
	protected void onResume() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		}
	    super.onResume();
	  }
    
    protected void setupSlidingMenu(){
		menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		btnMenu1 = (Button)findViewById(R.id.btnMenu1);
		btnMenu2 = (Button)findViewById(R.id.btnMenu2);
		btnMenu3 = (Button)findViewById(R.id.btnMenu3);
		btnMenu4 = (Button)findViewById(R.id.btnMenu4);
		btnMenu1.setOnClickListener(this);
		btnMenu2.setOnClickListener(this);
		btnMenu3.setOnClickListener(this);
		btnMenu4.setOnClickListener(this);
		btnMenuCatalog = (Button)findViewById(R.id.btnCatalog);
    	btnMenuCatalog.setOnClickListener(this);
	}
	public void gotoMenu (int id) {
		Class<?> cls = null;
		switch (id){
		case R.id.btnMenu1:
			cls = StartTrainingActivity.class;
			break;
		case R.id.btnMenu2:
			cls = ExersisesList.class;
			break;
		case R.id.btnMenu3:
			cls = HistoryActivity.class;
			break;
		case R.id.btnMenu4:
			cls = SettingsActivity.class;
			break;
		case R.id.btnCatalog:
			cls = CatalogActivity.class;
			break;
		}
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!menu.isMenuShowing()) {
				menu.showMenu();
			}
			menu.showContent();
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		gotoMenu(id);		
	}

}
