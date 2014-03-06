package com.nethergrim.combogymdiary.activities;

import com.google.android.gms.ads.AdView;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.yandex.metrica.Counter;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class BasicMenuActivity extends FragmentActivity implements
		OnClickListener {

	protected MenuDrawer mMenuDrawer;
	public final String LOG_TAG = "myLogs";
	protected Button btnMenu1, btnMenu2, btnMenu3, btnMenu4, btnMenuCatalog,
			btnMenuMeasurements, btnMenuGraphs, btnFAQ;
	protected SharedPreferences sPref;
	protected final static String TRAINING_AT_PROGRESS = "training_at_progress";
	protected final static String LIST_OF_SETS = "list_of_sets";
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
	protected final static String APPLICAITON_ID = "52ebc42807089e0f00000000";
	protected final static String MINUTES = "minutes";
	protected final static String SECONDS = "seconds";
	protected boolean isTrainingAtProgress;
	protected AdView adView;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		mMenuDrawer = MenuDrawer.attach(this);
		mMenuDrawer.setMenuView(R.layout.menu_frame_new);
		mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
		mMenuDrawer.setDrawerIndicatorEnabled(true);
		mMenuDrawer.setTouchBezelSize(3000);
		getActionBar().setDisplayShowHomeEnabled(true);
		initMenuButtons();
	}

	protected boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	protected void activateButton(Button btn){
		btn.setBackgroundColor(getResources().getColor(R.color.holo_blue_light));
		btn.setTextColor(getResources().getColor(R.color.white));
	}
	
	protected void deactivateButton(Button btn){
		btn.setBackgroundColor(getResources().getColor(R.color.light_gray));
		btn.setTextColor(getResources().getColor(R.color.abs__bright_foreground_holo_light));	
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
			btnMenu1.setText(getResources().getString(
					R.string.continue_training));
			btnMenu1.setBackgroundColor(getResources().getColor(
					R.color.holo_orange_dark_alpha_half));
		} else {
			btnMenu1.setText(getResources().getString(
					R.string.startTrainingButtonString));
			btnMenu1.setBackgroundColor(getResources().getColor(
					R.color.full_alpha));
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

	private void initMenuButtons() {
		btnMenu1 = (Button) findViewById(R.id.btnMenu1);
		btnMenu1.setOnClickListener(this);
		btnMenu2 = (Button) findViewById(R.id.btnMenu2);
		btnMenu2.setOnClickListener(this);
		btnMenu3 = (Button) findViewById(R.id.btnMenu3);
		btnMenu3.setOnClickListener(this);
		btnMenu4 = (Button) findViewById(R.id.btnMenu4);
		btnMenu4.setOnClickListener(this);
		btnFAQ = (Button)findViewById(R.id.btnFAQ);
		btnFAQ.setOnClickListener(this);
		btnMenuCatalog = (Button) findViewById(R.id.btnCatalog);
		btnMenuCatalog.setOnClickListener(this);
		btnMenuMeasurements = (Button) findViewById(R.id.btnMeasure);
		btnMenuMeasurements.setOnClickListener(this);
		btnMenuGraphs = (Button) findViewById(R.id.btnMenuGraphs);
		btnMenuGraphs.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mMenuDrawer.isActivated()) {
				mMenuDrawer.closeMenu();
			} else
				mMenuDrawer.toggleMenu();
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (mMenuDrawer.isMenuVisible()) {
			mMenuDrawer.closeMenu();
		} else {
			super.onBackPressed();
		}
	}

	protected boolean pressButton(int id, boolean toClose) {
		if (id == R.id.btnMenu1) {
			mMenuDrawer.closeMenu();
			if (isTrainingAtProgress) {
				Intent start = new Intent(this, TrainingAtProgress.class);
				String str = sPref.getString(TRAINING_NAME, "");
				start.putExtra("trainingName", str);
				startActivity(start);
			} else {
				Intent gotoStartTraining = new Intent(this,
						StartTrainingActivity.class);
				startActivity(gotoStartTraining);
				if (toClose)
					finish();
			}
			return true;
		} else if (id == R.id.btnMenu2) {
			mMenuDrawer.closeMenu();
			Intent gotoExersisesList = new Intent(this,
					ExersisesListActivity.class);
			startActivity(gotoExersisesList);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnMenu3) {
			mMenuDrawer.closeMenu();
			Intent gotoWorklog = new Intent(this, HistoryActivity.class);
			startActivity(gotoWorklog);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnMenu4) {
			mMenuDrawer.closeMenu();
			Intent gotoSettings = new Intent(this, SettingsActivity.class);
			startActivity(gotoSettings);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnCatalog) {
			mMenuDrawer.closeMenu();
			Intent gotoCatalog = new Intent(this, CatalogActivity.class);
			startActivity(gotoCatalog);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnMeasure) {

			Intent gotoMeasurements = new Intent(this,
					MeasurementsActivity.class);
			startActivity(gotoMeasurements);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnMenuGraphs) {
			mMenuDrawer.closeMenu();
			Intent intent = new Intent(this, GraphsActivity.class);
			startActivity(intent);
			if (toClose)
				finish();
			return true;
		} else if (id == R.id.btnFAQ){
			DialogInfo dialog = new DialogInfo();
			dialog.show(getFragmentManager(), "info");	
		}

		return false;
	}

	protected void onDestroy() {
		super.onDestroy();
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		
	}
}