package com.nethergrim.combogymdiary.activities;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MeasurementsDetailedActivity extends BasicMenuActivity {

	private String date;
	private DB db;
	private Cursor c;
	private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
	private String weightValue;
	private String longValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_measurements_detailed);
		Intent intent = getIntent();
		date = intent.getStringExtra("date");
		db = new DB(this);
		db.open();
		weightValue = getResources().getString(R.string.kg);
		longValue = getResources().getString(R.string.sm);

		getActionBar().setTitle(
				getResources().getString(R.string.measurements) + " - " + date);
		initTv();
		String weight = getResources().getString(R.string.weight);
		String tall = getResources().getString(R.string.tall);
		String chest = getResources().getString(R.string.chest);
		String waist = getResources().getString(R.string.waist);
		String hip = getResources().getString(R.string.hip);
		String leg = getResources().getString(R.string.leg);
		String calf = getResources().getString(R.string.calf);
		String arm = getResources().getString(R.string.arm);

		String[] cols = { DB.DATE, DB.PART_OF_BODY_FOR_MEASURING,
				DB.MEASURE_VALUE };
		String[] args = { date };
		c = db.getDataMeasures(cols, DB.DATE + "=?", args, null, null, DB.DATE);
		if (c.moveToFirst()) {
			Log.d(LOG_TAG, "found cursor size = " + c.getCount());
			do {
				String tmp_type = c.getString(1);
				if (tmp_type.equals(weight)) {
					tv1.setText(tmp_type + " - " + c.getString(2) + weightValue);
				} else if (tmp_type.equals(tall)) {
					tv2.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(chest)) {
					tv3.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(waist)) {
					tv4.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(hip)) {
					tv5.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(leg)) {
					tv6.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(calf)) {
					tv7.setText(tmp_type + " - " + c.getString(2) + longValue);
				} else if (tmp_type.equals(arm)) {
					tv8.setText(tmp_type + " - " + c.getString(2) + longValue);
				}
			} while (c.moveToNext());
		}

	}

	private void initTv() {
		tv1 = (TextView) findViewById(R.id.textView1_);
		tv2 = (TextView) findViewById(R.id.textView2_);
		tv3 = (TextView) findViewById(R.id.textView3_);
		tv4 = (TextView) findViewById(R.id.textView4_);
		tv5 = (TextView) findViewById(R.id.textView5_);
		tv6 = (TextView) findViewById(R.id.textView6_);
		tv7 = (TextView) findViewById(R.id.textView7_);
		tv8 = (TextView) findViewById(R.id.textView8_);
	}

	@Override
	public void onClick(View arg0) {
		pressButton(arg0.getId(),true);
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
