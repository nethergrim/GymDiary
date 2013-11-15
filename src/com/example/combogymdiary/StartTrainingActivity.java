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
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.nethergrim.gymdiary.R;

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
	
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_training);
        lvMain = (ListView) findViewById(R.id.lvStartTraining);        
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.startTrainingList);
        btnAddNew = (Button)findViewById(R.id.btnSave);
        btnAddNew.setOnClickListener(this);
        db = new DB(this);
		db.open();
		cursor_exe = db.getData_Exe_GroupBy(DB.TRA_NAME);		
		startManagingCursor(cursor_exe);
		cursor_exe.requery();
		String[] from = new String[] { DB.TRA_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item, cursor_exe, from, to);		
		lvMain.setAdapter(scAdapter);
	    registerForContextMenu(lvMain);  
	    lvMain.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) {
	          Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "+ id);
	          goToTraining(position);
	        }
	    });
	    sp = PreferenceManager.getDefaultSharedPreferences(this);
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
	    		Log.d(LOG_TAG, "Rewriting " + cdel.getString(1) + " at index " + cdel.getInt(0) + " to empty");
	    		db.updateRec_Exe(cdel.getInt(0), DB.TRA_NAME, "empty");
	    	} while (cdel.moveToNext());  
	    	Toast.makeText(this, "Deleted: "  + tmpstr, Toast.LENGTH_SHORT).show();
	    	cursor_exe.requery();
	      }	      
	      return true;
	    }
	    return super.onContextItemSelected(item);
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
	    }
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
}