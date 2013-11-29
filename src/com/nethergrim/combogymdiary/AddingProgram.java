package com.nethergrim.combogymdiary;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class AddingProgram extends Activity implements OnClickListener {

	Button btnAdd;
	EditText etName;
	ListView lvExe;
	final String LOG_TAG = "myLogs";
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
		
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_program);
        btnAdd = (Button) findViewById(R.id.buttonAddingProgram);
        btnAdd.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.etTimerValue);
        etName.setOnClickListener(this);
        ActionBar bar = getActionBar();
     //   bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.creating_program);
        lvExe = (ListView)findViewById(R.id.listView1);
        lvExe.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);        
        db = new DB(this);
		db.open();
		cursor = db.getAllData_Exe();		
		startManagingCursor(cursor);		
		String[] from = new String[] {DB.EXE_NAME};
		int[] to = new int[] { android.R.id.text1, };
		scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,
				cursor, from, to);
		lvExe.setAdapter(scAdapter);
	    registerForContextMenu(lvExe);	   
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
	    if (id == R.id.buttonAddingProgram) {
	    	String prgName = etName.getText().toString();
			long[] arrIDs = lvExe.getCheckedItemIds();
			if (!prgName.isEmpty()) 
			{
				for (int i = 0; i < arrIDs.length; i++)
				{
					db.updateRec_Exe((int) arrIDs[i], DB.TRA_NAME, prgName);					
				} 
			}
			super.onBackPressed();    			
		} else {
			gotoMenu(id);
		}
	}
		
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();	    
	  }
}
