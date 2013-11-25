package com.nethergrim.combogymdiary;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class HistoryDetailedActivity extends Activity {

	DB db;
	Cursor cursor;
	String trName = null;
	String trDate = null;
	int id = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DB(this);
		db.open();
		Intent intent = getIntent();	    
		trName 	= intent.getStringExtra("trName");        
		trDate 	= intent.getStringExtra("date");
		id 		= intent.getIntExtra("traID",0);
		setupActionBar();
		setupCursor();
		setupLayout();
	}

	private void setupActionBar() {
		getActionBar().setTitle(trName+" ("+trDate+")");
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setupCursor(){
		String[] cols = {DB.DATE, DB.TRA_NAME, DB.EXE_NAME, DB.WEIGHT, DB.REPS, DB.SET};
		String[] args = {trDate};
		cursor = db.getDataMain(cols, DB.DATE+"=?", args, null, null, null);
	}
	
	@SuppressLint("NewApi")
	private void setupLayout(){
		LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout llMain = new LinearLayout(this);
        llMain.setOrientation(LinearLayout.VERTICAL);
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
        setContentView(llMain, linLayoutParam);
        llMain.setGravity(Gravity.CENTER_HORIZONTAL);
        if (cursor.moveToFirst()){
        	do {
        		TextView tvNew = new TextView(this);
        		tvNew.setText(cursor.getString(2)); // вывод названия упражнения
        		tvNew.setLayoutParams(lpView);
        		llMain.addView(tvNew,lpView);
        		do {
        			TextView tvNewSet = new TextView(this);
        			tvNewSet.setText( ""+cursor.getInt(3)+"/"+cursor.getInt(4) );
        			llMain.addView(tvNewSet,lpView);
        		}while (cursor.moveToNext() && cursor.getInt(5) != 1);
        		cursor.moveToPrevious();
        		TextView divider = new TextView(this);
        		divider.setText("----------------------");
        		
        		llMain.addView(divider,lpView);
        	} while (cursor.moveToNext());
        }        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	  }
}
