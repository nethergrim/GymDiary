package com.example.combogymdiary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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

public class AddingExersises extends Activity implements OnClickListener {

	Button btnCreate; 				// кпопка "Сохранить"
	ListView lvExersices_list;		// список с упражнениями
	EditText etName, etTimer;		// поля для ввода имени и значения таймера
	String exeName = "", timerV = "";// название и таймер для изменения упражнения в базе
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
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adding_exersise);
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
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
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item,
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
	}
	
	protected void onResume() {
	    defaultTimer = sp.getString("etDefault", "" );
	    etTimer.setText(defaultTimer);
	    super.onResume();
	  }
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent gotoMain = new Intent(this, MainActivity.class);
			startActivity(gotoMain);
			break;
		}
		return false;
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
				db.addRec_Exe("empty", name , timer);
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
				Log.d(LOG_TAG, "Updating from " + exeName + " to " + name + " at ID " + exeID);
			    cursor.requery();			
				etName.setText("");
				etTimer.setText("");
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				Intent gotoExersisesList = new Intent (this,ExersisesList.class);
				startActivity(gotoExersisesList);
			}			
		}	
	}

	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }		
}
