package com.nethergrim.combogymdiary;

import android.os.Bundle;
import android.app.Activity;

public class DiskActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}




}
