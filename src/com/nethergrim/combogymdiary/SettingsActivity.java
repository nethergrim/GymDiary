package com.nethergrim.combogymdiary;

import com.nethergrim.combogymdiary.DialogRestoreFromBackup.MyInterface;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.DialogFragment;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements MyInterface {

	public static SettingsActivity sa;
	DialogFragment dlg2;
	protected SharedPreferences sPref;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		ActionBar bar = getActionBar();
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
		Intent intent = new Intent(this, DiskActivity.class);
		startActivity(intent);
	}

	@Override
	public void onChoose() {
		Backuper backUP = new Backuper();
		boolean yes = backUP.restoreBackup();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			return true;
		}
		return false;
	}

}
