package com.nethergrim.combogymdiary.activities;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryDetailedActivity extends Activity {

	private DB db;
	private Cursor cursor;
	private String trName = null;
	private String trDate = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			return true;
		}
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DB(this);
		db.open();
		Intent intent = getIntent();
		trName = intent.getStringExtra("trName");
		trDate = intent.getStringExtra("date");
		setupActionBar();
		setupCursor();
		setupLayout();
	}

	private void setupActionBar() {
		getActionBar().setTitle(trName + " (" + trDate + ")");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	private void setupCursor() {
		String[] cols = { DB.DATE, DB.TRA_NAME, DB.EXE_NAME, DB.WEIGHT,
				DB.REPS, DB.SET };
		String[] args = { trDate };
		cursor = db.getDataMain(cols, DB.DATE + "=?", args, null, null, null);
	}

	@SuppressLint("NewApi")
	private void setupLayout() {
		ScrollView scrollView = new ScrollView(this);
		LayoutParams lpView = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout llMain = new LinearLayout(this);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			scrollView.setBackground(getResources().getDrawable(
					R.drawable.cream_pixels_bitmap));
		}

		llMain.setOrientation(LinearLayout.VERTICAL);
		LayoutParams linLayoutParam = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setContentView(scrollView, linLayoutParam);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String item = sp.getString(BasicMenuActivityNew.MEASURE_ITEM, "1");
		String measureItem = "";
		if (item.equals("1")){
			measureItem = " (" + getResources().getStringArray(R.array.measure_items)[0] + ") ";
		} else if (item.equals("2")){
			measureItem = " (" + getResources().getStringArray(R.array.measure_items)[1] + ") ";
		}		
		scrollView.addView(llMain, linLayoutParam);
		llMain.setGravity(Gravity.CENTER_HORIZONTAL);
		if (cursor.moveToFirst()) {
			do {
				TextView tvNew = new TextView(this);
				tvNew.setText(cursor.getString(2));
				tvNew.setLayoutParams(lpView);
				llMain.addView(tvNew, lpView);
				do {
					TextView tvNewSet = new TextView(this);
					tvNewSet.setText("" + cursor.getInt(3) + measureItem + "/"
							+ cursor.getInt(4));
					llMain.addView(tvNewSet, lpView);
				} while (cursor.moveToNext() && cursor.getInt(5) != 1);
				cursor.moveToPrevious();
				View div = new View(this);
				LinearLayout.LayoutParams lparamDivider = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				lparamDivider.setMargins(20, 10, 20, 0);
				div.setLayoutParams(lparamDivider);
				div.setBackgroundColor(Color.GRAY);
				llMain.addView(div);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
