package com.nethergrim.combogymdiary;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class CatalogActivity extends Activity implements OnClickListener{
	Button btnMenu1,btnMenu2,btnMenu3,btnMenu4;
	private SlidingMenu menu;
	final String LOG_TAG = "myLogs";
	ExpandableListView elvMain;
	AdapterHelper ah;
	SimpleExpandableListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catalog);
		elvMain = (ExpandableListView)findViewById(R.id.elvMain);
		ah = new AdapterHelper(this);
        adapter = ah.getAdapter();
        elvMain.setAdapter(adapter);
        getActionBar().setTitle(getResources().getString(R.string.exe_catalog));
        
        elvMain.setOnChildClickListener(new OnChildClickListener() {
	      public boolean onChildClick(ExpandableListView parent, View v,
	          int groupPosition,   int childPosition, long id) {
	        Log.d(LOG_TAG, "onChildClick groupPosition = " + groupPosition + 
	                " childPosition = " + childPosition + 
	                " id = " + id);
	        return false;
	      }
	    });
        
        elvMain.setOnGroupClickListener(new OnGroupClickListener() {
	      public boolean onGroupClick(ExpandableListView parent, View v,
	          int groupPosition, long id) {
	        Log.d(LOG_TAG, "onGroupClick groupPosition = " + groupPosition + 
	                " id = " + id);
	        return false;
	      }
	    });
               
        elvMain.setOnGroupCollapseListener(new OnGroupCollapseListener() {
	      public void onGroupCollapse(int groupPosition) {
	        Log.d(LOG_TAG, "onGroupCollapse groupPosition = " + groupPosition);
	      }
	    });
        
        elvMain.setOnGroupExpandListener(new OnGroupExpandListener() {
	      public void onGroupExpand(int groupPosition) {
	        Log.d(LOG_TAG, "onGroupExpand groupPosition = " + groupPosition);
	      }
	    });
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

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		gotoMenu(id);		
	}


}
