package com.example.combogymdiary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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
        bar.setDisplayHomeAsUpEnabled(true);
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
	    }
    
       
    @Override
    public boolean onOptionsItemSelected (MenuItem item) 
    {
    	int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			Intent gotoMain = new Intent(this,MainActivity.class);
			startActivity(gotoMain);
		}
		return false;    	
    }
    
	@Override
	public void onClick(View arg0) {
	    int id = arg0.getId();
	    if (id == R.id.buttonAddingProgram) {
	    	String prgName = etName.getText().toString();
			long[] arrIDs = lvExe.getCheckedItemIds();
			/*
			SparseBooleanArray sbArray = lvExe.getCheckedItemPositions();			
			if (!prgName.isEmpty()) 
	    	{
	    		for (int i = 0 ; i < sbArray.size() ; i++) 
	    		{
	    			int key = sbArray.keyAt(i);
	    			Log.d(LOG_TAG, "key at "+i+" = "+key);
	    			if (sbArray.get(key))
	    			{
	    				Log.d(LOG_TAG, "GOT key at "+i+" = "+key);
	    				db.updateRec_Exe(key, DB.TRA_NAME, prgName);
	    			}
	    		}*/
			if (!prgName.isEmpty()) 
			{
				for (int i = 0; i < arrIDs.length; i++)
				{
					Log.d(LOG_TAG, "key at "+i+" = "+arrIDs[i]);
					db.updateRec_Exe((int) arrIDs[i], DB.TRA_NAME, prgName);					
				} 
			}
		    Intent gotoProgramList = new Intent (this,StartTrainingActivity.class);
			startActivity(gotoProgramList);	    			
		}
	}
		
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();	    
	  }
}
