package com.nethergrim.combogymdiary;


import net.simonvt.menudrawer.MenuDrawer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class BasicMenuActivity extends FragmentActivity implements OnClickListener {

	protected MenuDrawer mMenuDrawer;
    protected final String LOG_TAG = "myLogs";
    protected Button btnMenu1,btnMenu2,btnMenu3,btnMenu4,btnMenuCatalog,btnMenuMeasurements;
    protected SharedPreferences sPref;
    protected final static  String TRAINING_AT_PROGRESS = "training_at_progress";
    protected final static String LIST_OF_SETS = "list_of_sets";
    public final static String TRAINING_NAME = "training_name";
    public final static String TRA_ID = "training_id";
    public final static String TRAINING_LIST = "training_list";
    protected final  static  String MINUTES = "minutes";
    protected final  static String SECONDS = "seconds";
    protected boolean isTrainingAtProgress;
    
    
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        mMenuDrawer = MenuDrawer.attach(this);
        

        
        
        mMenuDrawer.setMenuView(R.layout.menu_frame);
        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        mMenuDrawer.setDrawerIndicatorEnabled(true);
        mMenuDrawer.setTouchBezelSize(3000);
        getActionBar().setDisplayShowHomeEnabled(true);
        initMenuButtons();
        
        sPref =  PreferenceManager.getDefaultSharedPreferences(this);
        
    }
    
    @Override
    protected void onResume(){
    	
        if (sPref.contains(TRAINING_AT_PROGRESS)){
        	isTrainingAtProgress = sPref.getBoolean(TRAINING_AT_PROGRESS, false);
        	
        }else {
        	Editor editor = sPref.edit();
        	editor.putBoolean(TRAINING_AT_PROGRESS, false);
        	editor.commit();
        }
        if (isTrainingAtProgress) {
        	btnMenu1.setText(getResources().getString(R.string.continue_training));
        	btnMenu1.setBackgroundColor(getResources().getColor(R.color.holo_orange_dark_alpha_half));
        }else {
        	btnMenu1.setText(getResources().getString(R.string.startTrainingButtonString));
        	btnMenu1.setBackgroundColor(getResources().getColor(R.color.full_alpha));
        }
        super.onResume();
    }
    
    private void initMenuButtons(){
    	btnMenu1 = (Button)findViewById(R.id.btnMenu1);
    	btnMenu1.setOnClickListener(this);
    	btnMenu2 = (Button)findViewById(R.id.btnMenu2);
    	btnMenu2.setOnClickListener(this);
    	btnMenu3 = (Button)findViewById(R.id.btnMenu3);
    	btnMenu3.setOnClickListener(this);
    	btnMenu4 = (Button)findViewById(R.id.btnMenu4);
    	btnMenu4.setOnClickListener(this);
    	btnMenuCatalog = (Button)findViewById(R.id.btnCatalog);
    	btnMenuCatalog.setOnClickListener(this);
    	btnMenuMeasurements=(Button)findViewById(R.id.btnMeasure);
    	btnMenuMeasurements.setOnClickListener(this);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mMenuDrawer.isActivated()) {
				mMenuDrawer.closeMenu();
			}else
				mMenuDrawer.toggleMenu();
			return true;
		}
		return false;
	}
    
    @Override
	public void onBackPressed() {
		if (mMenuDrawer.isMenuVisible()) {
			mMenuDrawer.closeMenu();
		} else {
			super.onBackPressed();
		}
	}
    
    protected boolean pressButton(int id) {
    	if (id == R.id.btnMenu1){
    		mMenuDrawer.closeMenu();
    		if (isTrainingAtProgress) {
    			Intent start = new Intent(this,TrainingAtProgress.class);
    			String str = sPref.getString(TRAINING_NAME, "");
    			Log.d(LOG_TAG, "putting extra: TRA_NAME == "+str);
    			start.putExtra("trainingName", str);
    			startActivity(start);
    		}else {
    			Intent gotoStartTraining = new Intent (this,StartTrainingActivity.class);
    			startActivity(gotoStartTraining);
    		}
    		return true;
    	} else if (id == R.id.btnMenu2){
    		mMenuDrawer.closeMenu();
    		Intent gotoExersisesList = new Intent (this,ExersisesList.class);
			startActivity(gotoExersisesList);
    		return true;
    	} else if (id == R.id.btnMenu3){
    		mMenuDrawer.closeMenu();
    		Intent gotoWorklog = new Intent (this,HistoryActivity.class);
			startActivity(gotoWorklog);
    		return true;
    	} else if (id == R.id.btnMenu4){
    		mMenuDrawer.closeMenu();
    		Intent gotoSettings = new Intent(this,SettingsActivity.class);
			startActivity(gotoSettings);
    		return true;
    	} else if (id == R.id.btnCatalog) {
    		mMenuDrawer.closeMenu();
    		Intent gotoCatalog = new Intent (this,CatalogActivity.class);
			startActivity(gotoCatalog);
    		return true;
    	} else if (id == R.id.btnMeasure){
    		mMenuDrawer.closeMenu();
    		Intent gotoMeasurements = new Intent (this,MeasurementsActivity.class);
			startActivity(gotoMeasurements);
    		return true;
    	}
    	return false;
    }
}
