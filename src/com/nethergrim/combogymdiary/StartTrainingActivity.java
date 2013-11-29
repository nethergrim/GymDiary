package com.nethergrim.combogymdiary;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class StartTrainingActivity extends Activity implements OnClickListener {

	final String LOG_TAG = "myLogs";
	private static final int CM_DELETE_ID = 1;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor_exe;
	Cursor cur;
	ListView lvMain;
	Button btnAddNew;
	SharedPreferences sp;
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_training);
        lvMain = (ListView) findViewById(R.id.lvStartTraining);        
        ActionBar bar = getActionBar();
       // bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.startTrainingList);
        btnAddNew = (Button)findViewById(R.id.btnSave);
        btnAddNew.setOnClickListener(this);
        db = new DB(this);
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(this);		
		cursor_exe = db.getData_Exe_GroupBy(DB.TRA_NAME);
		initList();  
		setupSlidingMenu();
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
	
    @SuppressWarnings("deprecation")
	private void initList () {
    	String[] from = new String[] { DB.TRA_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item, cursor_exe, from, to);		
		lvMain.setAdapter(scAdapter);
	    registerForContextMenu(lvMain);  
	    lvMain.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) {
	          goToTraining(position);
	        }
	    });
    }
    
	@SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		}
    	cursor_exe.requery();
    	super.onResume();
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v,
		      ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		  }
    
    @SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
	    if (item.getItemId() == CM_DELETE_ID) {
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      Cursor c = db.getData_Exe_GroupBy(DB.TRA_NAME);
	      for (int i = 0; i <= acmi.position; i++) 
	      {
	    	  c.moveToNext();
	      }
	      String tmpstr = c.getString(1);	      
	      String[] cols = {DB.COLUMN_ID,DB.TRA_NAME};
	      String[] args = {tmpstr};
	      Cursor cdel = db.getDataExe(cols,DB.TRA_NAME + "=?" , args, null, null, null);
	      if (cdel.moveToFirst())
	      {
	    	do {
	    		db.updateRec_Exe(cdel.getInt(0), DB.TRA_NAME, getString(R.string.empty));
	    	} while (cdel.moveToNext());  
	    	Toast.makeText(this, "Deleted: "  + tmpstr, Toast.LENGTH_SHORT).show();
	    	cursor_exe.requery();
	      }	      
	      return true;
	    }
	    return super.onContextItemSelected(item);
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
    
    public void goToTraining(int position) 
    {
    	String str = null;
    	Cursor cur1 = db.getData_Exe_GroupBy(DB.TRA_NAME);
    	if (cur1.moveToFirst()) 
    	{
    		for (int i = 1 ; i <= position; i++) 
        	{
        		cur1.moveToNext();
        	}
    		str = cur1.getString(1);    		
    	}    			
    	Intent intent_to_trainng = new Intent(this,TrainingAtProgress.class);
    	intent_to_trainng.putExtra("trainingName", str);
        startActivity(intent_to_trainng);
    }
    
	@Override
	public void onClick(View arg0) {
	    int id = arg0.getId();
	    switch (id)
	    {
	    case R.id.btnSave:
	    	Intent gotoAddingProgramActivity = new Intent(this,AddingProgram.class);
			startActivity(gotoAddingProgramActivity);
	    	break;
	    default:
	    	gotoMenu(id);
	    	break;
	    }
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
}