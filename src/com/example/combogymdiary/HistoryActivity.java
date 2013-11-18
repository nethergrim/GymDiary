package com.example.combogymdiary;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.support.v4.app.NavUtils;

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
		cursor = db.getData_Main_GroupBy(DB.DATE);// возможна проблема, если за один день 2 тренировки
		if (cursor.moveToFirst()) {
			size = cursor.getCount();
			Log.d(LOG_TAG,"cursor size = "+size);
			names = new String[size];
			dates = new String[size];
			int ii = 0;
			do {
				names[ii] = cursor.getString(1);
				dates[ii] = cursor.getString(3);
				Log.d(LOG_TAG,"added to array name:"+names[ii]+" date "+dates[ii]);				
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
		}
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
}
