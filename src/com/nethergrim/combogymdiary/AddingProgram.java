package com.nethergrim.combogymdiary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddingProgram extends BasicMenuActivity{

	private Button btnAdd;
	private EditText etName;
	private ListView lvExe;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor;
	// исправил под версию базы 3
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer.setContentView(R.layout.adding_program);
        btnAdd = (Button) findViewById(R.id.buttonAddingProgram);
        btnAdd.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.etTimerValue);
        getActionBar().setTitle(R.string.creating_program);
        lvExe = (ListView)findViewById(R.id.listView1);
        lvExe.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);        
        db = new DB(this);
		db.open();
		String[] cols = {DB.COLUMN_ID,DB.EXE_NAME,DB.TIMER_VALUE};
		cursor = db.getDataExe(cols, null, null, null, null, null);
		startManagingCursor(cursor);		
		String[] from = new String[] {DB.EXE_NAME};
		int[] to = new int[] { android.R.id.text1, };
		scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,
				cursor, from, to);
		lvExe.setAdapter(scAdapter);
	    }

    
	@Override
	public void onClick(View arg0) {
	    int id = arg0.getId();
	    pressButton(id);
	    if (id == R.id.buttonAddingProgram) {
	    	String prgName = etName.getText().toString();
			long[] arrIDs = lvExe.getCheckedItemIds();
			if (!prgName.isEmpty()) 
			{
				cursor.moveToFirst();
				String[] exersices = new String[arrIDs.length];
				for (int i = 0; i < exersices.length; i++) {
					cursor.moveToPosition( (int)arrIDs[i] - 1 );
					exersices[i] = cursor.getString(1);
					Log.d(LOG_TAG, "Added to exersices["+i+"] - "+cursor.getString(1));
				}
				
				db.addRec_Trainings(prgName, db.convertArrayToString(exersices) );
			}
			NavUtils.navigateUpFromSameTask(this);	
		} else {
		}
	}
		
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();	    
	  }
}
