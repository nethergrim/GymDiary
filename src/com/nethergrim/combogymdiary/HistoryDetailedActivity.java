package com.nethergrim.combogymdiary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HistoryDetailedActivity extends Activity implements OnClickListener {

	DB db;
	Cursor cursor;
	String trName = null;
	String trDate = null;
	int id = 0;
	
	private SlidingMenu menu;
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
	
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
		setupSlidingMenu();
	}
	
	protected void onResume() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		}
	    super.onResume();
	  }

	protected void setupSlidingMenu(){
		menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		btnMenu1 = (Button)findViewById(R.id.btnMenu1);
		btnMenu2 = (Button)findViewById(R.id.btnMenu2);
		btnMenu3 = (Button)findViewById(R.id.btnMenu3);
		btnMenu4 = (Button)findViewById(R.id.btnMenu4);
		btnMenu1.setOnClickListener(this);
		btnMenu2.setOnClickListener(this);
		btnMenu3.setOnClickListener(this);
		btnMenu4.setOnClickListener(this);
	}
	public void gotoMenu (int id) {
		Class<?> cls = null;
		switch (id){
		case R.id.btnMenu1:
			cls = StartTrainingActivity.class;
			break;
		case R.id.btnMenu2:
			cls = ExersisesList.class;
			break;
		case R.id.btnMenu3:
			cls = HistoryActivity.class;
			break;
		case R.id.btnMenu4:
			cls = SettingsActivity.class;
			break;
		}
		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}
	
	
	private void setupActionBar() {
		getActionBar().setTitle(trName+" ("+trDate+")");
	//	getActionBar().setDisplayHomeAsUpEnabled(true);
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
			if (!menu.isMenuShowing()) {
				menu.showMenu();
			}
			menu.showContent();
			return true;
		}
		return false;
	}

	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	  }

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		gotoMenu(id);		
	}
	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}
}
