package com.nethergrim.combogymdiary.activities;

import com.google.android.gms.ads.AdView;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class BasicMenuActivityNew extends FragmentActivity {
	protected final String LOG_TAG = "myLogs";
	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerList;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected String[] listButtons;

	protected SharedPreferences sPref;
	protected final static String TRAINING_AT_PROGRESS = "training_at_progress";
	protected final static String LIST_OF_SETS = "list_of_sets";
	protected final static String TRAINING_NAME = "training_name";
	protected final static String TRA_ID = "training_id";
	protected final static String TRAINING_LIST = "training_list";
	protected final static String TIMER_IS_ON = "timerIsOn";
	protected final static String MY_AD_UNIT_ID = "ca-app-pub-5652589022154086/4102541457";
	protected final static String MY_ACCOUNT_NAME = "account_name";
	protected final static String DRIVE_FOLDER_ID_ENCODED_TO_STRING = "drive_folder_id";
	protected final static String DRIVE_EXISTS = "drive_exists";
	protected final static String MARKET_LEAVED_FEEDBACK = "market_leaved_feedback";
	protected final static String DATABASE_FILLED = "database_filled";
	protected final static String AUTO_BACKUP_TO_DRIVE = "settingAutoBackup";
	protected final static String PROGRESS = "progress";
	protected final static String TRAININGS_DONE_NUM = "trainings_done_num";
	protected final static String USER_CLICKED_POSITION = "user_clicked_position";
	protected final static String APPLICAITON_ID = "52ebc42807089e0f00000000";
	protected final static String MINUTES = "minutes";
	protected final static String SECONDS = "seconds";
	protected AdView adView;
	protected boolean isTrainingAtProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		initStrings();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.my_list_item2, listButtons));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	private void initStrings() {
		listButtons = new String[8];
		listButtons[0] = getResources().getString(
				R.string.startTrainingButtonString);
		listButtons[1] = getResources().getString(
				R.string.excersisiesListButtonString);
		listButtons[2] = getResources().getString(R.string.training_history);
		listButtons[3] = getResources().getString(R.string.measurements);
		listButtons[4] = getResources().getString(R.string.exe_catalog);
		listButtons[5] = getResources().getString(R.string.statistics);
		listButtons[6] = getResources()
				.getString(R.string.settingsButtonString);
		listButtons[7] = getResources().getString(R.string.faq);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	public void selectItem(int position) {
		Log.d(LOG_TAG, "clicked menu position " + position);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	protected boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	protected void setView(View v) {
		FrameLayout content_frame = (FrameLayout) findViewById(R.id.content_frame);
		content_frame = (FrameLayout) findViewById(R.id.content_frame);
		content_frame.addView(v);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (sPref.contains(TRAINING_AT_PROGRESS)) {
			isTrainingAtProgress = sPref
					.getBoolean(TRAINING_AT_PROGRESS, false);

		} else {
			Editor editor = sPref.edit();
			editor.putBoolean(TRAINING_AT_PROGRESS, false);
			editor.commit();
		}
		if (isTrainingAtProgress) { // TODO
			// btnMenu1.setText(getResources().getString(
			// R.string.continue_training));
			// btnMenu1.setBackgroundColor(getResources().getColor(
			// R.color.holo_orange_dark_alpha_half));
		} else {
			// btnMenu1.setText(getResources().getString(
			// R.string.startTrainingButtonString));
			// btnMenu1.setBackgroundColor(getResources().getColor(
			// R.color.full_alpha));
		}
		Counter.sharedInstance().onResumeActivity(this);
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (adView != null) {
			adView.pause();
		}

		Counter.sharedInstance().onPauseActivity(this);
	}

	protected void onDestroy() {
		super.onDestroy();
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
