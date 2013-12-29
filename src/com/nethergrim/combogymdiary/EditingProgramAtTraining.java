package com.nethergrim.combogymdiary;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class EditingProgramAtTraining extends BasicMenuActivity  {

	protected ListView lvMain;
	private DB db;
	private long traID = 0;
	private SimpleCursorAdapter scAdapter;
	private Cursor cursor;
	private String traName;
	private Cursor traCursor;
	private String[] exercisesOld;
	private boolean ifAddingExe = false;
	private EditText etName;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_editing_program_at_training);
		lvMain = (ListView)findViewById(R.id.lvExers);
		lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Intent in = getIntent();
		db = new DB(this);
		db.open();
		etName = (EditText)findViewById(R.id.etNewNameOfProgram);
		ifAddingExe = in.getBooleanExtra("ifAddingExe",false);
		traName = in.getStringExtra("trName");
		traID = in.getLongExtra("trID", 0);
		Log.d(LOG_TAG, "traID == "+traID);
		cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
	    String[] from = new String[] { DB.EXE_NAME };
	    int[] to = new int[] { android.R.id.text1 };
	    scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,cursor,from,to);
	    lvMain.setAdapter(scAdapter);
	    if (ifAddingExe) {
	    	etName.setAlpha(0);
			getActionBar().setTitle(getResources().getString(R.string.add_an_exercise));
		} else {
			if (traID > 0) {
				etName.setAlpha(1);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.BELOW, R.id.etNewNameOfProgram);
				lvMain.setLayoutParams(lp);
				
				initData();
				setClicked();
			}
		}
	}
	
	private void initData(){
		String[] args = {""+traID};
		Cursor c = db.getDataTrainings(null, DB.COLUMN_ID+"=?", args, null, null, null);
	    c.moveToFirst();
	    getActionBar().setTitle(getResources().getString(R.string.editing_program)+":  "+c.getString(1));
	    etName.setText(c.getString(1));
	    exercisesOld = db.convertStringToArray(c.getString(2)) ;
	}
	
	private void setClicked() {
		if (cursor.moveToFirst()){
			int i = 0;
			do {
				for (int j = 0; j < exercisesOld.length; j++) {
					Log.d(LOG_TAG, "cursor.getString(2) == "+cursor.getString(2)+"\ncursor.getString(2).equals(exercisesOld[j] =="+cursor.getString(2).equals(exercisesOld[j]));
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
			if (ifAddingExe) {
				long[] arrIDs = lvMain.getCheckedItemIds();
				Intent intent = new Intent();
			    setResult(RESULT_OK, intent);
			    intent.putExtra("return_array_of_exersices", arrIDs);
			    finish();
			} else {
				long[] arrIDs = lvMain.getCheckedItemIds();
				

				Cursor cur = db.getDataExe(null, null, null, null, null, null);
				String[] args = new String[arrIDs.length];
				if (cur.moveToFirst()){
					int i = 0;
					do{
						for (int j = 0; j < arrIDs.length; j++){
							if ( cur.getInt(0) == arrIDs[j]){
								args[i] = cur.getString(2);
								Log.d(LOG_TAG, "args[i] =="+args[i]);
								i++;
							}
						}
					}while(cur.moveToNext());
					String newW = db.convertArrayToString(args);
					db.updateRec_Training((int)traID, 1, etName.getText().toString());
					db.updateRec_Training((int)traID, 2, newW);
				
				}
				
				
				
				
				
				Intent intent = new Intent();
			    setResult(RESULT_OK, intent);
			    finish();
			}
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
