package com.nethergrim.combogymdiary.activities;

import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogRestoreFromBackup;
import com.nethergrim.combogymdiary.dialogs.DialogRestoreFromBackup.MyInterface;
import com.nethergrim.combogymdiary.drive.DiskBackupActivity;
import com.nethergrim.combogymdiary.drive.DiskRestoreActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.DialogFragment;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements MyInterface {

	private DialogFragment dlg2;
	private DB db;
	public final String LOG_TAG = "myLogs";
	private final static int REQUEST_CODE_GET_FILE_FOR_RESTORE = 133;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		ActionBar bar = getActionBar();
		db = new DB(this);
		db.open();
		getActionBar().setDisplayShowHomeEnabled(false);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.settingsButtonString);
		dlg2 = new DialogRestoreFromBackup();
		Preference btnBackup = (Preference) findPreference("btnBackup");
		btnBackup
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						Backuper backUP = new Backuper();
						boolean yes = backUP.backupToSd();
						if (yes)
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(R.string.backuped),
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.backup_error),
									Toast.LENGTH_SHORT).show();

						DB db = new DB(getApplicationContext());
						db.open();
						db.close();
						return true;
					}
				});
		Preference btnRestore = (Preference) findPreference("btnRestore");
		btnRestore
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						dlg2.show(getFragmentManager(), "dlg2");
						return true;
					}
				});
		Preference btnDisk = (Preference) findPreference("btnBackupToDrive");
		btnDisk.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				gotoDisk();
				return true;
			}
		});

		Preference btnGoToMarket = (Preference) findPreference("btnGoToMarket");
		btnGoToMarket
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						goToMarket();
						return true;
					}
				});

		Preference btnEmail = (Preference) findPreference("btnEmail");
		btnEmail.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				sendEmail();
				return true;
			}
		});
		Preference btnVK = (Preference) findPreference("btnVK");
		btnVK.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {

				gotoVk();
				return true;
			}
		});

		Preference btnDriveRestore = (Preference) findPreference("btnDriveRestore");
		btnDriveRestore
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						gotoDriveRestore();
						return true;
					}
				});
	}

	protected void gotoDriveRestore() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Boolean isAtTraining = sp.getBoolean("training_at_progress", false);
		if (!isAtTraining) {
			Intent intent = new Intent(this, DiskRestoreActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_restoring),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void goToMarket() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + getPackageName())));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ getPackageName())));
		}
	}

	private void gotoVk() {
		Intent browser = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://vk.com/club_nethergrim"));
		startActivity(browser);
	}

	private void sendEmail() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		String[] to = { "c2q9450@gmail.com" };
		intent.putExtra(Intent.EXTRA_EMAIL, to);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				getResources().getString(R.string.app_name));
		startActivity(Intent.createChooser(intent,
				getResources().getString(R.string.send_email)));

	}

	private void gotoDisk() {
		Intent intent = new Intent(this, DiskBackupActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == REQUEST_CODE_GET_FILE_FOR_RESTORE) {
			String path = data.getData().getPath();

			Backuper backUP = new Backuper();
			boolean yes = backUP.restoreBackup(path);
			if (yes) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.restored),
						Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.restore_error),
						Toast.LENGTH_SHORT).show();
			DB db = new DB(getApplicationContext());
			db.open();
			db.close();
		}
	}

	@Override
	public void onChoose() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		Uri uri = Uri
				.parse(Environment.getExternalStorageDirectory().getPath());
		intent.setDataAndType(uri, "text/csv");
		startActivityForResult(Intent.createChooser(intent, "Open folder"),
				REQUEST_CODE_GET_FILE_FOR_RESTORE);
	}

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
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

}
