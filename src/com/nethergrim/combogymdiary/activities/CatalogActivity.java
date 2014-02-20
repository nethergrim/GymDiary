package com.nethergrim.combogymdiary.activities;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nethergrim.combogymdiary.AdapterHelper;
import com.nethergrim.combogymdiary.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

public class CatalogActivity extends BasicMenuActivity {

	private ExpandableListView elvMain;
	private AdapterHelper ah;
	private SimpleExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_catalog);
		elvMain = (ExpandableListView) findViewById(R.id.elvMain);
		ah = new AdapterHelper(this);
		adapter = ah.getAdapter();
		elvMain.setAdapter(adapter);
		getActionBar().setTitle(getResources().getString(R.string.exe_catalog));
		elvMain.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				gotoDetailed(groupPosition, childPosition, id);
				return false;
			}
		});
		elvMain.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});
		elvMain.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			public void onGroupCollapse(int groupPosition) {
			}
		});
		elvMain.setOnGroupExpandListener(new OnGroupExpandListener() {
			public void onGroupExpand(int groupPosition) {
			}
		});

		AdView adView = (AdView) this.findViewById(R.id.adView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		if (isNetworkAvailable()) {
			adView.setVisibility(View.VISIBLE);
		}
	}

	private void gotoDetailed(int groupPosition, int childPosition, long id) {
		Intent gotoDetailedActivity = new Intent(this,
				CatalogDetailedActivity.class);
		gotoDetailedActivity.putExtra("groupPosition", groupPosition);
		gotoDetailedActivity.putExtra("childPosition", childPosition);
		gotoDetailedActivity.putExtra("id", id);
		startActivity(gotoDetailedActivity);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		pressButton(id,true);
	}

}
