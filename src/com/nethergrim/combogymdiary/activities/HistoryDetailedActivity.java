package com.nethergrim.combogymdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class HistoryDetailedActivity extends Activity {

	private DB db;
	private Cursor cursor;
	private String trName = null;
	private String trDate = null;
	private TextView tvWeight, tvComment;
	private FrameLayout content_frame;
	private String measureItem;
	private int total = 0;

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
		setContentView(R.layout.activity_history_detailed);
		tvComment = (TextView) findViewById(R.id.tvComment);
		tvWeight = (TextView) findViewById(R.id.textViewWeightTOtal);
		content_frame = (FrameLayout) findViewById(R.id.content_frame);
		db = new DB(this);
		db.open();
		Intent intent = getIntent();
		trName = intent.getStringExtra("trName");
		trDate = intent.getStringExtra("date");
		setupActionBar();

		Cursor c = db.getCommentData(trDate);
		if (c.moveToFirst()) {
			Log.d("myLogs", c.getInt(4) + "");
			total = c.getInt(4);
			if (c.getString(2) != null) {
				tvComment.setText(getResources().getString(R.string.comment)
						+ " " + c.getString(2));
				tvComment.setVisibility(View.VISIBLE);
			} else {
				tvComment.setVisibility(View.GONE);
			}
		}

		c.close();
		setupCursor();
		setupLayout();

		tvWeight.setText(getResources().getString(
				R.string.total_weight_of_training)
				+ " " + total + measureItem);

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

	private void setupLayout() {
		ScrollView scrollView = new ScrollView(this);
		LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lpView.gravity = Gravity.CENTER;

		LinearLayout.LayoutParams lpData = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lpData.gravity = Gravity.CENTER;

		LinearLayout llMain = new LinearLayout(this);

		llMain.setOrientation(LinearLayout.VERTICAL);
		LayoutParams linLayoutParam = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		content_frame.addView(scrollView, linLayoutParam);

		boolean ifZero = false;
		if (total == 0)
			ifZero = true;

		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				8, getResources().getDisplayMetrics());

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String item = sp.getString(BasicMenuActivityNew.MEASURE_ITEM, "1");
		measureItem = "";
		if (item.equals("1")) {
			measureItem = " ("
					+ getResources().getStringArray(R.array.measure_items)[0]
					+ ") ";
		} else if (item.equals("2")) {
			measureItem = " ("
					+ getResources().getStringArray(R.array.measure_items)[1]
					+ ") ";
		}
		scrollView.addView(llMain, linLayoutParam);
		llMain.setGravity(Gravity.CENTER);

		int color = getResources().getColor(R.color.gray_dark);
		if (cursor.moveToFirst()) {
			do {

				LayoutInflater inflater = getLayoutInflater();
				View card = inflater.inflate(R.layout.item_detailed_history,
						null, false);
				TextView tvName = (TextView) card
						.findViewById(R.id.textViewExerciseName);
				LinearLayout llData = (LinearLayout) card
						.findViewById(R.id.linearLayoutForConent);
				llData.setGravity(Gravity.CENTER);
				tvName.setText(cursor.getString(2));
				tvName.setTextColor(color);

				lpView.setMargins(0, px, 0, 0);

				llMain.addView(card, lpView);
				do {
					TextView tvNewSet = new TextView(this);
					tvNewSet.setGravity(Gravity.CENTER);
					tvNewSet.setText("" + cursor.getInt(3) + measureItem + "/"
							+ cursor.getInt(4));
					tvNewSet.setTextColor(color);
					if (ifZero == true) {
						total += cursor.getInt(3) * cursor.getInt(4);
					}

					lpData.gravity = Gravity.CENTER;
					llData.addView(tvNewSet, lpData);
				} while (cursor.moveToNext() && cursor.getInt(5) != 1);
				cursor.moveToPrevious();

			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
