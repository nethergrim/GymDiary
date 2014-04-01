package com.nethergrim.combogymdiary.drive;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.yandex.metrica.Counter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BasicDriveService extends Service implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	protected static final String TAG = "BaseDriveService";
	public final static String DRIVE_EXISTS = "drive_exists";
	public final static String DRIVE_FOLDER_ID_ENCODED_TO_STRING = "drive_folder_id";
	protected static final String DRIVE_FOLDER_NAME = "Workout Diary Backups";
	protected static final String DRIVE_ID = "drive_id";
	protected boolean ifConnected = false;
	protected static final String EXTRA_ACCOUNT_NAME = "account_name";
	protected static final int REQUEST_CODE_RESOLUTION = 1;
	protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;
	private GoogleApiClient mGoogleApiClient;
	protected int failCount = 0;

	@Override
	public void onCreate() {
		super.onCreate();

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();

		return super.onStartCommand(intent, flags, startId);
	}

	protected void disconnect() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onConnected(Bundle arg0) {
		ifConnected = true;
		Log.d(TAG, "mGoogleApiClient connected");

	}

	@Override
	public void onDisconnected() {
		ifConnected = false;
		Log.d(TAG, "mGoogleApiClient disconnected");
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		ifConnected = false;
		if (failCount < 5) {
			if (mGoogleApiClient != null) {
				mGoogleApiClient.connect();
			}
		} else {
			Counter.sharedInstance().reportEvent("onConnectionFailed, stopping self");
			stopSelf();
		}
		
	}

	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}
}
