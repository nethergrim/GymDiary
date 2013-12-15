package com.nethergrim.combogymdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MeasurementsActivity extends BasicMenuActivity {

	ListView lvMeasurements;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_measurements);
		getActionBar().setTitle(getResources().getString(R.string.measurements));
		lvMeasurements = (ListView)findViewById(R.id.lvMeasurements);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.measurements, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_measurements: {
			Intent intent = new Intent (this,AddingMeasurementActivity.class);
			startActivity(intent);
			return true;
		}
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
	public void onClick(View arg0) {
		pressButton(arg0.getId());
	}



}
