package com.nethergrim.combogymdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.R;

public class CatalogDetailedActivity extends Activity {

	private TextView tvMain;
	private ImageView imageV;
	private int groupPosition, childPosition;
	private String[] pectoral = null;
	private String[] legs = null;
	private String[] back = null;
	private String[] deltoids = null;
	private String[] biceps = null;
	private String[] triceps = null;
	private String[] abs = null;

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
		setContentView(R.layout.activity_catalog_detailed);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		tvMain = (TextView) findViewById(R.id.tvInfoExe);
		imageV = (ImageView) findViewById(R.id.ivMain);
		Intent intent = getIntent();
		groupPosition = intent.getIntExtra("groupPosition", 0);
		childPosition = intent.getIntExtra("childPosition", 0);
		pectoral = getResources().getStringArray(R.array.exercisesArrayChest);
		legs = getResources().getStringArray(R.array.exercisesArrayLegs);
		back = getResources().getStringArray(R.array.exercisesArrayBack);
		deltoids = getResources().getStringArray(
				R.array.exercisesArrayShoulders);
		biceps = getResources().getStringArray(R.array.exercisesArrayBiceps);
		triceps = getResources().getStringArray(R.array.exercisesArrayTriceps);
		abs = getResources().getStringArray(R.array.exercisesArrayAbs);
		initInfo();
	}

	private void initInfo() {
		switch (groupPosition) {
		case 0:
			initGroup0();
			getActionBar().setTitle(pectoral[childPosition]);
			break;
		case 1:
			getActionBar().setTitle(legs[childPosition]);
			initGroup1();
			break;
		case 2:
			getActionBar().setTitle(back[childPosition]);
			initGroup2();
			break;
		case 3:
			getActionBar().setTitle(deltoids[childPosition]);
			initGroup3();
			break;
		case 4:
			getActionBar().setTitle(biceps[childPosition]);
			initGroup4();
			break;
		case 5:
			getActionBar().setTitle(triceps[childPosition]);
			initGroup5();
			break;
		case 6:
			getActionBar().setTitle(abs[childPosition]);
			initGroup6();
			break;
		}
	}

	private void initGroup0() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_0));
			tvMain.setText(getResources().getString(R.string.ex_0_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_1));
			tvMain.setText(getResources().getString(R.string.ex_0_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_2));
			tvMain.setText(getResources().getString(R.string.ex_0_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_3));
			tvMain.setText(getResources().getString(R.string.ex_0_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_4));
			tvMain.setText(getResources().getString(R.string.ex_0_4));
			break;
		case 5:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_5));
			tvMain.setText(getResources().getString(R.string.ex_0_5));
			break;
		case 6:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_6));
			tvMain.setText(getResources().getString(R.string.ex_0_6));
			break;
		case 7:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_7));
			tvMain.setText(getResources().getString(R.string.ex_0_7));
			break;
		case 8:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_8));
			tvMain.setText(getResources().getString(R.string.ex_0_8));
			break;
		case 9:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_9));
			tvMain.setText(getResources().getString(R.string.ex_0_9));
			break;
		case 10:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_10));
			tvMain.setText(getResources().getString(R.string.ex_0_10));
			break;
		case 11:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_11));
			tvMain.setText(getResources().getString(R.string.ex_0_11));
			break;
		case 12:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_12));
			tvMain.setText(getResources().getString(R.string.ex_0_12));
			break;
		case 13:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_0_13));
			tvMain.setText(getResources().getString(R.string.ex_0_13));
			break;
		}
	}

	private void initGroup1() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_0));
			tvMain.setText(getResources().getString(R.string.ex_1_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_1));
			tvMain.setText(getResources().getString(R.string.ex_1_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_2));
			tvMain.setText(getResources().getString(R.string.ex_1_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_3));
			tvMain.setText(getResources().getString(R.string.ex_1_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_4));
			tvMain.setText(getResources().getString(R.string.ex_1_4));
			break;
		case 5:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_5));
			tvMain.setText(getResources().getString(R.string.ex_1_5));
			break;
		case 6:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_6));
			tvMain.setText(getResources().getString(R.string.ex_1_6));
			break;
		case 7:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_7));
			tvMain.setText(getResources().getString(R.string.ex_1_7));
			break;
		case 8:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_8));
			tvMain.setText(getResources().getString(R.string.ex_1_8));
			break;
		case 9:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_9));
			tvMain.setText(getResources().getString(R.string.ex_1_9));
			break;
		case 10:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_10));
			tvMain.setText(getResources().getString(R.string.ex_1_10));
			break;
		case 11:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_11));
			tvMain.setText(getResources().getString(R.string.ex_1_11));
			break;
		case 12:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_12));
			tvMain.setText(getResources().getString(R.string.ex_1_12));
			break;
		case 13:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_1_13));
			tvMain.setText(getResources().getString(R.string.ex_1_13));
			break;
		}
	}

	private void initGroup2() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_0));
			tvMain.setText(getResources().getString(R.string.ex_2_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_1));
			tvMain.setText(getResources().getString(R.string.ex_2_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_2));
			tvMain.setText(getResources().getString(R.string.ex_2_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_3));
			tvMain.setText(getResources().getString(R.string.ex_2_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_4));
			tvMain.setText(getResources().getString(R.string.ex_2_4));
			break;
		case 5:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_5));
			tvMain.setText(getResources().getString(R.string.ex_2_5));
			break;
		case 6:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_6));
			tvMain.setText(getResources().getString(R.string.ex_2_6));
			break;
		case 7:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_7));
			tvMain.setText(getResources().getString(R.string.ex_2_7));
			break;
		case 8:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_8));
			tvMain.setText(getResources().getString(R.string.ex_2_8));
			break;
		case 9:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_9));
			tvMain.setText(getResources().getString(R.string.ex_2_9));
			break;
		case 10:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_10));
			tvMain.setText(getResources().getString(R.string.ex_2_10));
			break;
		case 11:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_2_11));
			tvMain.setText(getResources().getString(R.string.ex_2_11));
			break;
		}
	}

	private void initGroup3() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_0));
			tvMain.setText(getResources().getString(R.string.ex_3_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_1));
			tvMain.setText(getResources().getString(R.string.ex_3_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_2));
			tvMain.setText(getResources().getString(R.string.ex_3_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_3));
			tvMain.setText(getResources().getString(R.string.ex_3_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_4));
			tvMain.setText(getResources().getString(R.string.ex_3_4));
			break;
		case 5:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_5));
			tvMain.setText(getResources().getString(R.string.ex_3_5));
			break;
		case 6:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_6));
			tvMain.setText(getResources().getString(R.string.ex_3_6));
			break;
		case 7:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_7));
			tvMain.setText(getResources().getString(R.string.ex_3_7));
			break;
		case 8:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_8));
			tvMain.setText(getResources().getString(R.string.ex_3_8));
			break;
		case 9:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_3_9));
			tvMain.setText(getResources().getString(R.string.ex_3_9));
			break;
		}
	}

	private void initGroup4() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_0));
			tvMain.setText(getResources().getString(R.string.ex_4_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_1));
			tvMain.setText(getResources().getString(R.string.ex_4_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_2));
			tvMain.setText(getResources().getString(R.string.ex_4_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_3));
			tvMain.setText(getResources().getString(R.string.ex_4_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_4));
			tvMain.setText(getResources().getString(R.string.ex_4_4));
			break;
		case 5:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_5));
			tvMain.setText(getResources().getString(R.string.ex_4_5));
			break;
		case 6:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_4_6));
			tvMain.setText(getResources().getString(R.string.ex_4_6));
			break;
		}
	}

	private void initGroup5() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_5_0));
			tvMain.setText(getResources().getString(R.string.ex_5_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_5_1));
			tvMain.setText(getResources().getString(R.string.ex_5_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_5_2));
			tvMain.setText(getResources().getString(R.string.ex_5_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_5_3));
			tvMain.setText(getResources().getString(R.string.ex_5_3));
			break;
		}
	}

	private void initGroup6() {
		switch (childPosition) {
		case 0:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_6_0));
			tvMain.setText(getResources().getString(R.string.ex_6_0));
			break;
		case 1:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_6_1));
			tvMain.setText(getResources().getString(R.string.ex_6_1));
			break;
		case 2:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_6_2));
			tvMain.setText(getResources().getString(R.string.ex_6_2));
			break;
		case 3:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_6_3));
			tvMain.setText(getResources().getString(R.string.ex_6_3));
			break;
		case 4:
			imageV.setImageDrawable(getResources().getDrawable(
					R.drawable.ex_6_4));
			tvMain.setText(getResources().getString(R.string.ex_6_4));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
