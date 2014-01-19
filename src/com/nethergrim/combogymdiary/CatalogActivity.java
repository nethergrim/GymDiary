package com.nethergrim.combogymdiary;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;



public class CatalogActivity extends BasicMenuActivity{
	
	final String LOG_TAG = "myLogs";
	ExpandableListView elvMain;
	AdapterHelper ah;
	SimpleExpandableListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_catalog);
		elvMain = (ExpandableListView)findViewById(R.id.elvMain);
		ah = new AdapterHelper(this);
        adapter = ah.getAdapter();
        elvMain.setAdapter(adapter);
        getActionBar().setTitle(getResources().getString(R.string.exe_catalog));
        elvMain.setOnChildClickListener(new OnChildClickListener() {
        	
	      public boolean onChildClick(ExpandableListView parent, View v,
	          int groupPosition,   int childPosition, long id) {
	        Log.d(LOG_TAG,"onChildClick groupPosition = "+groupPosition+" childPosition = "+childPosition + 
	                " id = " + id);
	        gotoDetailed(groupPosition, childPosition, id);	        
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
        
        AdView adView = (AdView)this.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
	}
	
	private void gotoDetailed(int groupPosition, int childPosition, long id) {
		Intent gotoDetailedActivity = new Intent(this,CatalogDetailedActivity.class);
		gotoDetailedActivity.putExtra("groupPosition", groupPosition);
		gotoDetailedActivity.putExtra("childPosition", childPosition);
		gotoDetailedActivity.putExtra("id", id);
		startActivity(gotoDetailedActivity);
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id);		
	}


}
