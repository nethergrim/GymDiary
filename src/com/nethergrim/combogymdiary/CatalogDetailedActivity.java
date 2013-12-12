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
		Intent intent = getIntent();	    
		groupPosition = intent.getIntExtra("groupPosition", 0);
		childPosition = intent.getIntExtra("childPosition", 0);
		id 			  = intent.getLongExtra("id", 0);
		Log.d(LOG_TAG, "got intent: groupPosition = "+groupPosition
				+" childPosition = " + childPosition 
				+" id = "+id);
		initInfo();
		getActionBar().setTitle("");
	}

	private void initInfo(){
		switch (groupPosition){
		case 0:
			initGroup0();
			break;
		case 1:
			initGroup1();
			break;
		case 2:
			initGroup2();
			break;
		case 3:
			initGroup3();
			break;
		case 4:
			initGroup4();
			break;
		case 5:
			initGroup5();
			break;
		case 6:
			initGroup6();
			break;
		}
	}
	
	private void initGroup0(){
		switch (childPosition){
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(R.drawable.ex_0_0));
			tvMain.setText(getResources().getString(R.string.ex_0_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(R.drawable.ex_0_1));
			tvMain.setText(getResources().getString(R.string.ex_0_1));
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		case 11:
			break;
		case 12:
			break;
		case 13:
			break;			
		}
	}
	private void initGroup1(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		case 11:
			break;
		case 12:
			break;
		case 13:
			break;			
		}
	}
	private void initGroup2(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		case 11:
			break;			
		}
	}
	private void initGroup3(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			break;
		case 8:
			break;
		case 9:
			break;
		case 10:
			break;
		case 11:
			break;
		case 12:
			break;			
		}
	}
	private void initGroup4(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;			
		}
	}
	private void initGroup5(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;		
		}
	}
	private void initGroup6(){
		switch (childPosition){
		case 0:
			break;
		case 1:
			break;		
		}
	}
	
	
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);		
	}



}
