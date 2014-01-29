package com.nethergrim.combogymdiary;

import android.os.Bundle;
import android.view.View;

public class TempActivity extends BasicMenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mMenuDrawer.setContentView(R.layout.activity_temp);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);

	}

}
