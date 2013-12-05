package com.nethergrim.combogymdiary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class AddingExersises extends Activity implements OnClickListener {

	Button btnCreate; 				
	ListView lvExersices_list;		
	EditText etName, etTimer;		
	String exeName = "", timerV = "";
	int exePosition = 0;
	long exeID=0;
	String defaultTimer;
	final String LOG_TAG = "myLogs";
	Boolean editOrNot = false;	
	private static final int CM_DELETE_ID = 1;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	SharedPreferences sp;
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adding_exersise);
		ActionBar bar = getActionBar();
		//bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.create_new_exercise);
		btnCreate = (Button) findViewById(R.id.btnSave);
		btnCreate.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etTimerValue);
		etName.setOnClickListener(this);
		etTimer = (EditText) findViewById(R.id.editText2);
		etTimer.setOnClickListener(this);
		lvExersices_list = (ListView) findViewById(R.id.lvEx);		
		db = new DB(this);
		db.open();
		cursor = db.getAllData_Exe();
		cursor.requery();
		startManagingCursor(cursor);
		String[] from = new String[] {DB.EXE_NAME};
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item2,
				cursor, from, to);
		lvExersices_list.setAdapter(scAdapter);
	    registerForContextMenu(lvExersices_list);   
	    Intent intent = getIntent();	    
        exeName = intent.getStringExtra("exeName");        
        timerV = intent.getStringExtra("timerValue");        
        exePosition = intent.getIntExtra("exePosition", 0);
        exeID = intent.getLongExtra("exeID", 0);
        if ( exeName != null && timerV != null )  {
        	editOrNot = true;
        	}
        
        if (editOrNot) 
		{
			etName.setText(exeName);
			etTimer.setText(timerV);
		}
        sp = PreferenceManager.getDefaultSharedPreferences(this);
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
	
	
	protected void onResume() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		}
	    defaultTimer = sp.getString("etDefault", "60" );
	    etTimer.setText(defaultTimer);
	    super.onResume();
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
		  }

	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
	    if (item.getItemId() == CM_DELETE_ID) {
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      db.delRec_Exe(acmi.id);
	      cursor.requery();
	      return true;
	    }
	    return super.onContextItemSelected(item);
	  }
	
		@SuppressWarnings("deprecation")
		@Override
	public void onClick(View arg0) {
		String name = etName.getText().toString();
		String timer = etTimer.getText().toString();
		
		int id = arg0.getId();
		if (id == R.id.btnSave && editOrNot == false){			
			if (!name.isEmpty() && !timer.isEmpty()){
				db.addRec_Exe(R.string.empty+"", name , timer);
			    cursor.requery();			
				etName.setText("");
				etTimer.setText("");
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				Intent gotoExersisesList = new Intent (this,ExersisesList.class);
				startActivity(gotoExersisesList);
			}
		} else if (id == R.id.btnSave && editOrNot == true)	{
			if (!name.isEmpty() && !timer.isEmpty()){
				db.updateRec_Exe((int) exeID, DB.EXE_NAME, name);
				db.updateRec_Exe((int) exeID, DB.TIMER_VALUE, timer);
			    cursor.requery();			
				etName.setText("");
				etTimer.setText("");
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				Intent gotoExersisesList = new Intent (this,ExersisesList.class);
				startActivity(gotoExersisesList);
			}			
		}else {
			gotoMenu(id);
		}
	}

	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }		
}
