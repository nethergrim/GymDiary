package com.nethergrim.combogymdiary.activities;

import com.google.android.gms.ads.AdView;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.fragments.CatalogFragment;
import com.nethergrim.combogymdiary.fragments.ExerciseListFragment;
import com.nethergrim.combogymdiary.fragments.HistoryFragment;
import com.nethergrim.combogymdiary.fragments.MeasurementsFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment;
import com.nethergrim.combogymdiary.fragments.StatisticsFragment;
import com.yandex.metrica.Counter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
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
	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String LIST_OF_SETS = "list_of_sets";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "training_id";
	public final static String TRAINING_LIST = "training_list";
	public final static String TIMER_IS_ON = "timerIsOn";
	public final static String MY_AD_UNIT_ID = "ca-app-pub-5652589022154086/4102541457";
	public final static String MY_ACCOUNT_NAME = "account_name";
	public final static String DRIVE_FOLDER_ID_ENCODED_TO_STRING = "drive_folder_id";
	public final static String DRIVE_EXISTS = "drive_exists";
	public final static String MARKET_LEAVED_FEEDBACK = "market_leaved_feedback";
	public final static String DATABASE_FILLED = "database_filled";
	public final static String AUTO_BACKUP_TO_DRIVE = "settingAutoBackup";
	public final static String PROGRESS = "progress";
	public final static String TRAININGS_DONE_NUM = "trainings_done_num";
	public final static String USER_CLICKED_POSITION = "user_clicked_position";
	public final static String APPLICAITON_ID = "52ebc42807089e0f00000000";
	public final static String MINUTES = "minutes";
	public final static String SECONDS = "seconds";
	protected AdView adView;
	protected boolean isTrainingAtProgress;
	protected FrameLayout content_frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		initStrings();
		content_frame = (FrameLayout) findViewById(R.id.content_frame);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.menu_list_item, listButtons));
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
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			selectItem(0);
		}

		StartTrainingFragment fragment = new StartTrainingFragment();
		getFragmentManager().beginTransaction()
				.add(R.id.content_frame, fragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	public void selectItem(int position) { // TODO here select fragment
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new StartTrainingFragment();
			break;
		case 1:
			fragment = new ExerciseListFragment();
			break;
		case 6:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case 7:
			DialogInfo dialog = new DialogInfo();
			dialog.show(getFragmentManager(), "info");
			break;
		case 2:
			fragment = new HistoryFragment();
			break;
		case 3:
			fragment = new MeasurementsFragment();
			break;
		case 4:
			fragment = new CatalogFragment();
			break;
		case 5:
			fragment = new StatisticsFragment();
			break;
		}
		if (fragment != null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}
		invalidateOptionsMenu();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	protected boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
		if (isTrainingAtProgress) {
		} else {
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
		if (adView != null) {
			adView.destroy();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
