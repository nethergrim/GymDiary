package com.nethergrim.combogymdiary;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class HistoryActivity extends BasicMenuActivity {
	
	private ListView lvMain;
	private String[] names;
	private String[] dates;
	private DB db;
	private static final int CM_DELETE_ID = 1;
	private Cursor cursor;
	private int size = 0;
	SimpleAdapter adapter;
	HashMap<String, Object> map;
	ArrayList<HashMap<String, Object>> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_history);
		setupActionBar();
		db = new DB(this);
		db.open();
		lvMain = (ListView)findViewById(R.id.lvMainHistory);
		cursor = db.getData_Main_GroupBy(DB.DATE);
		if (cursor.moveToFirst()) {
			size = cursor.getCount();
			names = new String[size];
			dates = new String[size];
			int ii = 0;
			do {
				names[ii] = cursor.getString(1);
				dates[ii] = cursor.getString(3);		
				ii++;				
			}while (cursor.moveToNext());
			data = new ArrayList<HashMap<String, Object>>(names.length);
			
			for (int i = 0; i < names.length; i++) {
				map = new HashMap<String, Object>();
				map.put("NameAndDate", names[i]+" ("+dates[i]+")");
				data.add(map);
			}
			String[] from = {"NameAndDate"};
			int[] to = { R.id.tvCatName };
			adapter = new SimpleAdapter(this, data, R.layout.list_with_arrow,from, to);
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
		registerForContextMenu(lvMain);
	}

	
	public void onCreateContextMenu(ContextMenu menu, View v,
		      ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		  }

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	    if (item.getItemId() == CM_DELETE_ID) {	      
	    	int pos = acmi.position;
	    	cursor.moveToFirst();
			while (cursor.getPosition() < pos) {
				cursor.moveToNext();
			}
			String date = cursor.getString(3);
	    	String[] args = {date};
	    	
	    	Cursor tmp = db.getDataMain(null, DB.DATE+"=?", args, null, null, null) ;
	    	if (tmp.moveToFirst()) {
	    		
	    		do {
	    			db.delRec_Main( tmp.getInt(0) );
	    		} while (tmp.moveToNext());
	    		Log.d(LOG_TAG, "pos == "+pos+" data.getPos == "+data.get(pos));
	    		data.remove(pos);
	    		adapter.notifyDataSetChanged();
	    	}
	    	
	    	return true;
	    }	    
	    return super.onContextItemSelected(item);
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
	    startActivity(intent_history_detailed);	
	}

	private void setupActionBar() {
		ActionBar bar = getActionBar();
		bar.setTitle(R.string.training_history);
	}
	
	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	  }

	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);		
	}
}
