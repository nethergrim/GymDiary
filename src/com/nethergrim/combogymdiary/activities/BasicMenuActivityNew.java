package com.nethergrim.combogymdiary.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.ads.AdView;
import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.TrainingService;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining.MyInterface;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.drive.DriveCreateFolderActivity;
import com.nethergrim.combogymdiary.drive.DriveAutoBackupService;
import com.nethergrim.combogymdiary.fragments.CatalogFragment;
import com.nethergrim.combogymdiary.fragments.ExerciseListFragment;
import com.nethergrim.combogymdiary.fragments.HistoryFragment;
import com.nethergrim.combogymdiary.fragments.MeasurementsFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment;
import com.nethergrim.combogymdiary.fragments.StatisticsFragment;
import com.nethergrim.combogymdiary.fragments.TrainingFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment.OnSelectedListener;
import com.yandex.metrica.Counter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
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

public class BasicMenuActivityNew extends FragmentActivity implements
		OnSelectedListener, MyInterface {
	protected final String LOG_TAG = "myLogs";
	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerList;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected String[] listButtons;

	protected SharedPreferences sPref;
	public final static String TOTAL_WEIGHT = "total_weight";
	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String COMMENT_TO_TRAINING = "comment_to_training";
	public final static String MEASURE_ITEM = "measureItem";
	public final static String LIST_OF_SETS = "list_of_sets";
	public final static String TRAINING_ID = "training_id";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "tra_id";
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
	protected FrameLayout content_frame;
	private int FRAGMENT_NUMBER = 0;
	private final static String FRAGMENT_ID = "fragment_id";
	private static boolean IF_TRAINING_STARTED = false;
	private SharedPreferences sp;
	private ArrayAdapter<String> adapter;
	private int previouslyChecked = 0;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

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
		adapter = new ArrayAdapter<String>(this, R.layout.menu_list_item,
				listButtons);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
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
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		Fragment frag = null;
		if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
			frag = new TrainingFragment();
			Bundle args = new Bundle();
			args.putInt(TRAINING_ID, sp.getInt(TRA_ID, 0));
			frag.setArguments(args);
			listButtons[0] = getResources().getString(
					R.string.continue_training);
			adapter.notifyDataSetChanged();
			set_TRAINING_STARTED(true);
		} else {
			frag = new StartTrainingFragment();
		}
		if (frag != null)
			getFragmentManager().beginTransaction()
					.add(R.id.content_frame, frag).commit();
		mDrawerList.setItemChecked(0, true);

		if (savedInstanceState == null) {
			selectItem(0);
		}
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

	public void selectItem(int position) {

		mDrawerLayout.closeDrawer(mDrawerList);
		Fragment fragment = null;
		switch (position) {
		case 0:
			FRAGMENT_NUMBER = 0;
			if (get_TRAINING_STARTED()) {
				fragment = new TrainingFragment();
				Bundle args = new Bundle();
				args.putInt(TRAINING_ID, sp.getInt(TRA_ID, 0));
				fragment.setArguments(args);
				listButtons[0] = getResources().getString(
						R.string.continue_training);
				adapter.notifyDataSetChanged();
			} else {
				fragment = new StartTrainingFragment();
			}
			previouslyChecked = 0;
			break;
		case 1:
			FRAGMENT_NUMBER = 1;
			fragment = new ExerciseListFragment();
			previouslyChecked = 1;
			break;
		case 6:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			mDrawerList.setItemChecked(previouslyChecked, true);
			break;
		case 7:
			DialogInfo dialog = new DialogInfo();
			dialog.show(getFragmentManager(), "info");
			mDrawerList.setItemChecked(previouslyChecked, true);
			break;
		case 2:
			FRAGMENT_NUMBER = 2;
			fragment = new HistoryFragment();
			previouslyChecked = 2;
			break;
		case 3:
			FRAGMENT_NUMBER = 3;
			fragment = new MeasurementsFragment();
			previouslyChecked = 3;
			break;
		case 4:
			FRAGMENT_NUMBER = 4;
			fragment = new CatalogFragment();
			previouslyChecked = 4;
			break;
		case 5:
			FRAGMENT_NUMBER = 5;
			fragment = new StatisticsFragment();
			previouslyChecked = 5;
			break;
		}
		if (fragment != null) {
			mDrawerList.setItemChecked(position, true);
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
		Counter.sharedInstance().onResumeActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Counter.sharedInstance().onPauseActivity(this);
	}

	@Override
	public void onSaveInstanceState(Bundle save) {
		save.putInt(FRAGMENT_ID, FRAGMENT_NUMBER);
		super.onSaveInstanceState(save);
	}

	public void onRestoreInstanceState(Bundle restore) {
		selectItem(restore.getInt(FRAGMENT_ID));
		super.onRestoreInstanceState(restore);
	}

	@Override
	public void onTrainingSelected(int id) {
		TrainingFragment newFragment = new TrainingFragment();
		Bundle args = new Bundle();
		args.putInt(TRAINING_ID, id);
		newFragment.setArguments(args);

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, newFragment).commit();
		set_TRAINING_STARTED(true);
		listButtons[0] = getResources().getString(R.string.continue_training);
		adapter.notifyDataSetChanged();
	}

	public static boolean get_TRAINING_STARTED() {
		return IF_TRAINING_STARTED;
	}

	public static void set_TRAINING_STARTED(boolean iF_TRAINING_STARTED) {
		IF_TRAINING_STARTED = iF_TRAINING_STARTED;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onChoose() {
		DB db = new DB(this);
		db.open();
		Cursor tmpCursor = db.getDataMain(null, null, null, null, null, null);
		if (tmpCursor.getCount() > 10) {
			Backuper backUP = new Backuper();
			backUP.backupToSd();
		}
		db.close();

		sp.edit().putBoolean(TRAINING_AT_PROGRESS, false).apply();
		sp.edit().putInt(USER_CLICKED_POSITION, 0).apply();
		sp.edit().putInt(TrainingFragment.CHECKED_POSITION, 0).apply();
		int total = sp.getInt(TOTAL_WEIGHT, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		if (!sp.getString(COMMENT_TO_TRAINING, "").equals("")) {
			db.addRecComment(date, sp.getString(COMMENT_TO_TRAINING, ""), total);
		} else {
			db.addRecComment(date, null, total);// TODO
		}
		sp.edit().putString(COMMENT_TO_TRAINING, "").apply();
		sp.edit().putInt(TOTAL_WEIGHT, 0).apply();

		stopService(new Intent(this, TrainingService.class));

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				AUTO_BACKUP_TO_DRIVE, true)) {
//			Intent backupIntent = new Intent(this,
//					DiskCreateFolderActivity.class);
//			startActivity(backupIntent);
			
			Intent backup = new Intent (this, DriveAutoBackupService.class);
			startService(backup); // TODO здесь проверить, что бы нормально работал автобекап
		}

		if (sp.contains(TRAININGS_DONE_NUM)) {
			int tmp = sp.getInt(TRAININGS_DONE_NUM, 0);
			tmp++;
			sp.edit().putInt(TRAININGS_DONE_NUM, tmp).apply();
		} else {
			sp.edit().putInt(TRAININGS_DONE_NUM, 1).apply();
		}
		set_TRAINING_STARTED(false);
		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new StartTrainingFragment())
				.commit();
		listButtons[0] = getResources().getString(
				R.string.startTrainingButtonString);
		adapter.notifyDataSetChanged();

	}
}
