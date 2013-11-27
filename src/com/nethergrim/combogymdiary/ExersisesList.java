package com.nethergrim.combogymdiary;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class ExersisesList extends Activity implements OnClickListener {

	final String LOG_TAG = "myLogs";
	ListView lvExersices_list;
	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID = 2;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor_exe;
	int State = 0; // 1 - deleting, 2 - editing
	Button btnCreate;
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
	
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exersises_list); 
        ActionBar bar = getActionBar();
        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        bar.setTitle(R.string.excersisiesListButtonString);
        bar.setDisplayHomeAsUpEnabled(true);
        lvExersices_list = (ListView) findViewById(R.id.listView11);
        db = new DB(this);
		db.open();
		cursor_exe = db.getAllData_Exe();
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { R.id.tvText, };
		cursor_exe.requery();
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item2,
				cursor_exe, from, to);
		lvExersices_list.setAdapter(scAdapter);
	    registerForContextMenu(lvExersices_list);
	    lvExersices_list.setOnItemClickListener(new OnItemClickListener() 
	    	{
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) 
	        	{
		          Log.d(LOG_TAG, "In ExercisesList itemClick: position = " + position + ", id = "+ id);
		          goToEditExe(position, id);    
		        }
	    	});
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
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
    
	public void onCreateContextMenu(ContextMenu menu, View v,
		      ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		    menu.add(0,CM_EDIT_ID,0,R.string.edit);
		  }

	public void goToEditExe(int position,long ID) 
	{
		Cursor cur_edit_exe = db.getAllData_Exe();
		if (cur_edit_exe.moveToFirst())
		{
			while (cur_edit_exe.getPosition() < position) {
				Log.d(LOG_TAG,"cursor cur_edit_exe stands on: " + cur_edit_exe.getString(2) + " position " + position);
				cur_edit_exe.moveToNext();
				Log.d(LOG_TAG,"cursor cur_edit_exe moved to: " + cur_edit_exe.getString(2) + " position " + position);
			}
			String name = cur_edit_exe.getString(2);
			Toast.makeText(this, "Editing: "  + name, Toast.LENGTH_SHORT).show();
			Log.d(LOG_TAG,"going to edit: " + name);
			String timV = cur_edit_exe.getString(3);			
			Intent intent_exe_edit = new Intent(this,AddingExersises.class);
			intent_exe_edit.putExtra("exeName", name);
			intent_exe_edit.putExtra("timerValue", timV);   
			intent_exe_edit.putExtra("exePosition", position);
			intent_exe_edit.putExtra("exeID", ID);
			Log.d(LOG_TAG,"put extra to AddingExersises: exeName " + name + " timerValue " + timV + " position " + position);
	        startActivity(intent_exe_edit);
		}		
	}
	
	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	    if (item.getItemId() == CM_DELETE_ID) {	      
	      db.delRec_Exe(acmi.id);
	      Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	      cursor_exe.requery();
	      return true;
	    }else if (item.getItemId() == CM_EDIT_ID) 
	    {
	    	goToEditExe(acmi.position,acmi.id);
	    	return true;
	    }	    
	    return super.onContextItemSelected(item);
	  }
    
	@Override
	public void onClick(View arg0) {
	    switch (arg0.getId()) {
	    case R.id.btnCreate:
	    	Intent gotoAddingExersisesActivity = new Intent(this,AddingExersises.class);
	    	Log.d(LOG_TAG, "going to Adding Exercise");
			startActivity(gotoAddingExersisesActivity);			
	    	break;
	    default:
	    	gotoMenu(arg0.getId());
	    	break;
	    }	    
	}
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
	
}
