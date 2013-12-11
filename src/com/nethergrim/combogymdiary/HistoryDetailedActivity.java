package com.nethergrim.combogymdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryDetailedActivity extends BasicMenuActivity {

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
	}

	private void setupCursor(){
		String[] cols = {DB.DATE, DB.TRA_NAME, DB.EXE_NAME, DB.WEIGHT, DB.REPS, DB.SET};
		String[] args = {trDate};
		cursor = db.getDataMain(cols, DB.DATE+"=?", args, null, null, null);
	}
	
	@SuppressLint("NewApi")
	private void setupLayout(){
		ScrollView scrollView = new ScrollView(this);
		LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		scrollView.setBackground(getResources().getDrawable(R.drawable.cream_pixels_bitmap));
        LinearLayout llMain = new LinearLayout(this);
        llMain.setOrientation(LinearLayout.VERTICAL);
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mMenuDrawer.setContentView(scrollView, linLayoutParam);
        scrollView.addView(llMain,linLayoutParam);
        llMain.setGravity(Gravity.CENTER_HORIZONTAL);
        if (cursor.moveToFirst()){
        	do {
        		TextView tvNew = new TextView(this);
        		tvNew.setText(cursor.getString(2)); 
        		tvNew.setLayoutParams(lpView);
        		llMain.addView(tvNew,lpView);
        		do {
        			TextView tvNewSet = new TextView(this);
        			tvNewSet.setText( ""+cursor.getInt(3)+"/"+cursor.getInt(4) );
        			llMain.addView(tvNewSet,lpView);
        		}while (cursor.moveToNext() && cursor.getInt(5) != 1);
        		cursor.moveToPrevious();
        		View div = new View(this);
        		LinearLayout.LayoutParams lparamDivider = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        		lparamDivider.setMargins(20, 10, 20, 0);
        		div.setLayoutParams(lparamDivider);
        		div.setBackgroundColor(Color.GRAY);
        		llMain.addView(div);
        	} while (cursor.moveToNext());
        }        
	}
	


	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	  }

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);		
	}

}
