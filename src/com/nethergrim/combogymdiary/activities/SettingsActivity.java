package com.nethergrim.combogymdiary.activities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.nethergrim.combogymdiary.AdEnabler;
import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogRestoreFromBackup;
import com.nethergrim.combogymdiary.dialogs.DialogRestoreFromBackup.MyInterface;
import com.nethergrim.combogymdiary.googledrive.BaseDriveActivity;
import com.nethergrim.combogymdiary.googledrive.DriveBackupActivity;
import com.nethergrim.combogymdiary.googledrive.DriveRestoreActivity;
import com.yandex.metrica.Counter;

public class SettingsActivity extends PreferenceActivity implements MyInterface {

	private DialogFragment dlg2;
	public String RINGTONE = "ringtone";
	private DB db;
	public final String LOG_TAG = "myLogs";
	private final static int REQUEST_CODE_GET_FILE_FOR_RESTORE = 133;
	private SharedPreferences sp;
	private IInAppBillingService mService;
	private Preference btnRemoveAds;

	private static final char[] symbols = new char[36];

	static {
		for (int idx = 0; idx < 10; ++idx)
			symbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
			symbols[idx] = (char) ('a' + idx - 10);
	}

	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);

		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		ActionBar bar = getActionBar();
		db = new DB(this);
		db.open();
		bindService(new Intent(
				"com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
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

		Preference btnSelectSound = (Preference) findPreference("toPlaySound");
		btnSelectSound
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						gotoSelectSound();
						return true;
					}
				});

		btnRemoveAds = (Preference) findPreference("ads");
		if (AdEnabler.IsPaid()) {
			btnRemoveAds.setEnabled(false);
		}
		btnRemoveAds
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						removeAds();
						return true;
					}
				});
	}

	protected void removeAds() {
		RandomString randomString = new RandomString(36);
		String payload = randomString.nextString();
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					"remove_ad", "inapp", payload);
			int response = buyIntentBundle.getInt("BILLING_RESPONSE_RESULT_OK");
			if (response == 0) {
				Log.d(LOG_TAG, "buyIntent response OK");

				PendingIntent pendingIntent = buyIntentBundle
						.getParcelable("BUY_INTENT");
				try {
					startIntentSenderForResult(pendingIntent.getIntentSender(),
							1001, new Intent(), Integer.valueOf(0),
							Integer.valueOf(0), Integer.valueOf(0));

				} catch (SendIntentException e) {
					Counter.sharedInstance().reportError("", e);
					Log.d(LOG_TAG, e.getMessage());
				}
			}

		} catch (RemoteException e) {
			Counter.sharedInstance().reportError("", e);
			Log.d(LOG_TAG, e.getMessage());
		}

	}

	protected void gotoSelectSound() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
		this.startActivityForResult(intent, 5);
	}

	protected void gotoDriveRestore() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Boolean isAtTraining = sp.getBoolean("training_at_progress", false);
		if (!isAtTraining) {

			Intent intent = new Intent(this, DriveRestoreActivity.class);
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
		Intent intent = new Intent(this, DriveBackupActivity.class);
		intent.putExtra(BaseDriveActivity.KEY_AUTOBACKUP, false);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1001) {
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			if (resultCode == RESULT_OK) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					Counter.sharedInstance().reportEvent(
							"bought the " + sku + ".");
					AdEnabler.setPaid(true);
					PreferenceManager.getDefaultSharedPreferences(this).edit()
							.putBoolean("ad", true).apply();

				} catch (JSONException e) {
					Counter.sharedInstance().reportError("", e);
				}
			}
		}

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
		if (resultCode == Activity.RESULT_OK && requestCode == 5) {
			Uri uri = data
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null) {
				Log.d(LOG_TAG, "putting uri to alarm: " + uri.toString());
				sp.edit().putString(RINGTONE, uri.toString()).apply();
			} else {
			}
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
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}

	public class RandomString {

		private final Random random = new Random();

		private final char[] buf;

		public RandomString(int length) {
			if (length < 1)
				throw new IllegalArgumentException("length < 1: " + length);
			buf = new char[length];
		}

		public String nextString() {
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = symbols[random.nextInt(symbols.length)];
			return new String(buf);
		}

	}

	public final class SessionIdentifierGenerator {

		@SuppressLint("TrulyRandom")
		private SecureRandom random = new SecureRandom();

		public String nextSessionId() {
			return new BigInteger(130, random).toString(32);
		}

	}

}
