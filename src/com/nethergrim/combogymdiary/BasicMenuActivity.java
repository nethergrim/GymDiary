package com.nethergrim.combogymdiary;


import net.simonvt.menudrawer.MenuDrawer;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

public abstract class BasicMenuActivity extends FragmentActivity implements OnClickListener {

	protected MenuDrawer mMenuDrawer;
    final String LOG_TAG = "myLogs";
    Button btnMenu1,btnMenu2,btnMenu3,btnMenu4,btnMenuCatalog,btnMenuMeasurements;
    
    
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        mMenuDrawer = MenuDrawer.attach(this);
        mMenuDrawer.setMenuView(R.layout.menu_frame);
        mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        mMenuDrawer.setDrawerIndicatorEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        initMenuButtons();
        mMenuDrawer.setTouchBezelSize(3000);
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
    		Log.d(LOG_TAG, "Menu Button 1 pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoStartTraining = new Intent (this,StartTrainingActivity.class);
			startActivity(gotoStartTraining);
    		return true;
    	} else if (id == R.id.btnMenu2){
    		Log.d(LOG_TAG, "Menu Button 2 pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoExersisesList = new Intent (this,ExersisesList.class);
			startActivity(gotoExersisesList);
    		return true;
    	} else if (id == R.id.btnMenu3){
    		Log.d(LOG_TAG, "Menu Button 3 pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoWorklog = new Intent (this,HistoryActivity.class);
			startActivity(gotoWorklog);
    		return true;
    	} else if (id == R.id.btnMenu4){
    		Log.d(LOG_TAG, "Menu Button 4 pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoSettings = new Intent(this,SettingsActivity.class);
			startActivity(gotoSettings);
    		return true;
    	} else if (id == R.id.btnCatalog) {
    		Log.d(LOG_TAG, "Menu btnCatalog pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoCatalog = new Intent (this,CatalogActivity.class);
			startActivity(gotoCatalog);
    		return true;
    	} else if (id == R.id.btnMeasure){
    		Log.d(LOG_TAG, "Menu btnMeasure pressed");
    		mMenuDrawer.closeMenu();
    		Intent gotoMeasurements = new Intent (this,MeasurementsActivity.class);
			startActivity(gotoMeasurements);
    		return true;
    	}
    	return false;
    }
}
