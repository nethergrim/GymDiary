package com.nethergrim.combogymdiary;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class StartTrainingActivity extends BasicMenuActivity {

	private static final int CM_DELETE_ID = 1;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor_exe;
	private ListView lvMain;
	private Button btnAddNew;
	// исправил под версию базы 3, проверить!
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer.setContentView(R.layout.start_training);
        lvMain = (ListView) findViewById(R.id.lvStartTraining);        
        getActionBar().setTitle(R.string.startTrainingList);
        btnAddNew = (Button)findViewById(R.id.btnSave);
        btnAddNew.setOnClickListener(this);
        db = new DB(this);
		db.open();
		cursor_exe = db.getDataTrainings(null, null, null, null, null, null);
		initList();  
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
	      if (cursor_exe.moveToFirst()){
	    	  for (int i = 0; i <= acmi.position; i++) 
		      {
		    	  cursor_exe.moveToNext();
		      }
		      int id = cursor_exe.getPosition();	      
	
		      db.delRec_Trainings(id);
		      Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
		      cursor_exe.requery();
		      return true;
	      }
	    }
	    return super.onContextItemSelected(item);
	  }    
    
    public void goToTraining(int position) 
    {
    	String str = null;

    	if (cursor_exe.moveToFirst()) 
    	{
    		for (int i = 1 ; i <= position; i++) 
        	{
    			cursor_exe.moveToNext();
        	}
    		str = cursor_exe.getString(1);    		
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
	    	pressButton(id);
	    	break;
	    }
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
}