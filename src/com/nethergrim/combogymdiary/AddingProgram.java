package com.nethergrim.combogymdiary;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddingProgram extends BasicMenuActivity implements LoaderCallbacks<Cursor>{

	private Button btnAdd;
	private EditText etName;
	private ListView lvExe;
	private DB db;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;


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
 		String[] from = new String[] {DB.EXE_NAME};
 		int[] to = new int[] { android.R.id.text1, };
		cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);	
		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice,null, from, to,0);
		lvExe.setAdapter(adapter);

		
		AdView adView = (AdView)this.findViewById(R.id.adView4);
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .addTestDevice("TEST_DEVICE_ID")
	    .build();
	    adView.loadAd(adRequest);
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
					exersices[i] = cursor.getString(2);
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
