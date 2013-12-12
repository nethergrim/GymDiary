package com.nethergrim.combogymdiary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogDetailedActivity extends BasicMenuActivity {

	
	TextView tvMain;
	ImageView imageV;
	int groupPosition,childPosition;
	long id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_catalog_detailed);
		tvMain = (TextView)findViewById(R.id.tvInfoExe);
		imageV = (ImageView)findViewById(R.id.ivMain);
		imageV.setImageDrawable(getResources().getDrawable(R.drawable.prised));
		tvMain.setText("тут текст типаIntent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  Intent intent = getIntent();	  ");
		Intent intent = getIntent();	    
		groupPosition = intent.getIntExtra("groupPosition", 0);
		childPosition = intent.getIntExtra("childPosition", 0);
		id 			  = intent.getLongExtra("id", 0);
		Log.d(LOG_TAG, "got intent: groupPosition = "+groupPosition
				+" childPosition = " + childPosition 
				+" id = "+id);
		
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);		
	}



}
