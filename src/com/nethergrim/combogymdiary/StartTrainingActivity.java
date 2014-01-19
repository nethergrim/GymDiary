package com.nethergrim.combogymdiary;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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

public class StartTrainingActivity extends BasicMenuActivity  implements LoaderCallbacks<Cursor>{

	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID	  = 2;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor_exe;
	private ListView lvMain;
	private Button btnAddNew;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  initUi();
	}
	
	private void initUi(){
		
        mMenuDrawer.setContentView(R.layout.start_training);
        lvMain = (ListView) findViewById(R.id.lvStartTraining);        
        getActionBar().setTitle(R.string.startTrainingList);
        btnAddNew = (Button)findViewById(R.id.btnSave);
        btnAddNew.setOnClickListener(this);
        db = new DB(this);
		db.open();
		initTrainings(); // if training table is empty
		cursor_exe = db.getDataTrainings(null, null, null, null, null, null);
		initList();  
		
		AdView adView = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	    
	    getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
    }
	
	private void initTrainings(){
		Cursor size = db.getDataTrainings(null, null, null, null, null, null);
		if (size.getCount() < 1) { 
			Cursor c = db.getDataExe(null, null, null, DB.TRA_NAME, null, null);
			if (c.moveToFirst()){	
				do {
					String[] args = {c.getString(1)};
					Cursor cur_local = db.getDataExe(null, DB.TRA_NAME + "=?", args, null, null, null);
					if (cur_local.moveToFirst()){
						String[] exercices = new String[cur_local.getCount()];
						int i = 0;
						do {
							exercices[i] = cur_local.getString(2);
							i++;
						} while(cur_local.moveToNext());
						String exes = db.convertArrayToString(exercices);
						String tmp = c.getString(1);
						Log.d(LOG_TAG, "trying to add rec to trainings: "+ tmp+" \n " + exes);
						db.addRec_Trainings(c.getString(1), exes);
					}
				}while (c.moveToNext());
			}
		}
	}
	
	private void initList () {
    	String[] from = new String[] { DB.TRA_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(this, R.layout.my_list_item, null, from, to,0);		
		lvMain.setAdapter(scAdapter);
		
	    registerForContextMenu(lvMain);  
	    lvMain.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id) {
	          goToTraining((int)id);
	        }
	    });
    }

	@Override
    protected void onResume() {
		getSupportLoaderManager().getLoader(0).forceLoad();
    	super.onResume();
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
	    return new MyCursorLoader(this, db);
	  }

	  @Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	    scAdapter.swapCursor(cursor);
	  }

	  @Override
	public void onLoaderReset(Loader<Cursor> loader) {
	  }
	  
	static class MyCursorLoader extends CursorLoader {

	    DB db;
	    Cursor cursor;
	    
	    public MyCursorLoader(Context context, DB db) {
	      super(context);
	      this.db = db;
	    }
	    
	    @Override
	    public Cursor loadInBackground() {
	    	cursor = db.getDataTrainings(null, null, null, null, null, null);
	      return cursor;
	    }
	  }
	
	
    public void onCreateContextMenu(ContextMenu menu, View v,
		      ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		    menu.add(1, CM_EDIT_ID, 0, R.string.edit);
		  }
    

	public boolean onContextItemSelected(MenuItem item) {
	    if (item.getItemId() == CM_DELETE_ID) {
	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	      if(cursor_exe.moveToFirst()){
	    	  for (int i = 0; i < acmi.position; i++) 
		      {
		    	  cursor_exe.moveToNext();
		      }
		      int id = cursor_exe.getInt(0);	      
		      db.delRec_Trainings(id);
		      Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
		      getSupportLoaderManager().getLoader(0).forceLoad();
		      return true;
	      }
	    } else if (item.getItemId() == CM_EDIT_ID){
	    	AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	    	long id = acmi.id;
	    	Intent intent = new Intent(this,EditingProgramAtTraining.class);
			intent.putExtra("trID", id);
			intent.putExtra("ifAddingExe",false);
			startActivityForResult(intent, 1);
			getSupportLoaderManager().getLoader(0).forceLoad();
	    	return true;
	    }
	    return super.onContextItemSelected(item);
	  }    
    
    @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	  }
    
    
    public void goToTraining(int id) 
    {

    	Log.d(LOG_TAG, "going to start training ID == "+id);

    	if (cursor_exe.moveToFirst()) {
    		String str = null;
    		do {
    			if (cursor_exe.getInt(0) == id) {
    				str = cursor_exe.getString(1);
    			}
    		} while (cursor_exe.moveToNext());
    		
    		
    		
    		Intent intent_to_trainng = new Intent(this,TrainingAtProgress.class);
    		if (!str.isEmpty())
        		intent_to_trainng.putExtra("trainingName", str);
            startActivity(intent_to_trainng);
    	}
    	
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