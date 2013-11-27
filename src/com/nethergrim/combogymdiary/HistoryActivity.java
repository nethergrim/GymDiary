package com.nethergrim.combogymdiary;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity {

	final String LOG_TAG = "myLogs";
	ListView lvMain;
	String[] names;
	String[] dates;
	DB db;
	Cursor cursor;
	int size = 0;
	
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
		bar.setDisplayHomeAsUpEnabled(true);
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
			NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return false;
	}
}
