package com.nethergrim.combogymdiary;

import java.util.ArrayList;
import java.util.HashMap;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity implements OnClickListener{

	final String LOG_TAG = "myLogs";
	ListView lvMain;
	String[] names;
	String[] dates;
	DB db;
	Cursor cursor;
	int size = 0;
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setupActionBar();
		db = new DB(this);
		db.open();
		lvMain = (ListView)findViewById(R.id.lvMainHistory);
		cursor = db.getData_Main_GroupBy(DB.DATE);
		if (cursor.moveToFirst()) {
			size = cursor.getCount();
			Log.d(LOG_TAG,"cursor size = "+size);
			names = new String[size];
			dates = new String[size];
			int ii = 0;
			do {
				names[ii] = cursor.getString(1);
				dates[ii] = cursor.getString(3);		
				ii++;				
			}while (cursor.moveToNext());

			ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>(names.length);
			HashMap<String, Object> map;
			for (int i = 0; i < names.length; i++) {
				map = new HashMap<String, Object>();
				map.put("NameAndDate", names[i]+" ("+dates[i]+")");
				data.add(map);
			}
			String[] from = {"NameAndDate"};
			int[] to = { R.id.tvCatName };
			SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.list_with_arrow,from, to);
			lvMain.setAdapter(adapter);
			lvMain.setOnItemClickListener(new OnItemClickListener() 
	    	{
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) 
	        	{
		          goToDetailed(position, id);
		        }
	    	});
		}
		setupSlidingMenu();
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
		}
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
	
	public void goToDetailed(int position,long ID) 
	{
		cursor.moveToFirst();
		while (cursor.getPosition() < position) {
			cursor.moveToNext();
		}
		String date = cursor.getString(3);
		String trName = cursor.getString(1);			
		Intent intent_history_detailed = new Intent(this,HistoryDetailedActivity.class);
		intent_history_detailed.putExtra("date", date);
		intent_history_detailed.putExtra("trName", trName);
		intent_history_detailed.putExtra("traID", ID);
		Log.d(LOG_TAG,"put extra to AddingExersises: date " + date + " trName " + trName + " traID " + ID);
	    startActivity(intent_history_detailed);	
	}

	

	private void setupActionBar() {
		ActionBar bar = getActionBar();
	//	bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.worklogString);
	}
	
	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
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
