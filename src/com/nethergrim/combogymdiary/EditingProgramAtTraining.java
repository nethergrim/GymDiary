package com.nethergrim.combogymdiary;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class EditingProgramAtTraining extends BasicMenuActivity  {

	protected ListView lvMain;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor;
	private String traName;
	private Cursor traCursor;
	private String[] exercisesOld;
	private boolean ifAddingExe = false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_editing_program_at_training);
		lvMain = (ListView)findViewById(R.id.lvExers);
		lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Intent in = getIntent();
		traName = in.getStringExtra("trName");
		ifAddingExe = in.getBooleanExtra("ifAddingExe");
		if (ifAddingExe) {
			getActionBar().setTitle("");//TODO здесь дописать титул а ля "добавление упражнения в тренировку"
			String[] a = {traName};
			Cursor c = db.getDataTrainings(null,DB.TRA_NAME+"=?",a,null,null,null);
			c.moveToFirst();
			
			
			String[] args = db.convertStringToArray(c.getString(2));
			cursor = db.getDataExe(null, DB.EXE_NAME+"!=?", args, null, null, DB.EXE_NAME);
		} else { 	 // этот случай, если полное редактирование программы
			cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
			initData();
			setClicked();
		}
	    db = new DB(this);
	    db.open();
	    String[] from = new String[] { DB.EXE_NAME };
	    int[] to = new int[] { android.R.id.text1 };
	    scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,cursor,from,to);
	    
	}
	
	private void initData(){
		lvMain.setAdapter(scAdapter);
	    String[] args = {traName};
	    traCursor = db.getDataTrainings(null, DB.TRA_NAME+"=?", args, null, null, null);
	    traCursor.moveToFirst();
	    exercisesOld = db.convertStringToArray(traCursor.getString(2)) ;
	}
	
	private void setClicked() {
		if (cursor.moveToFirst()){
			int i = 0;
			do {
				for (int j = 0; j < exercisesOld.length; j++) {
					if ( cursor.getString(2).equals(exercisesOld[j]) ){
						lvMain.setItemChecked(i,true);
					}
				}
				i++;
			} while(cursor.moveToNext());
		}
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editing_program_at_training, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionSaveEdited:
			long[] arrIDs = lvMain.getCheckedItemIds();
			
			Intent intent = new Intent();
		    setResult(RESULT_OK, intent);
		    finish();
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		pressButton(arg0.getId());		
	}
	  
	  protected void onDestroy() {
		    super.onDestroy();
		    db.close();
		  }


}
