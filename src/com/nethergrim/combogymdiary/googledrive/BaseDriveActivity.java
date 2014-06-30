package com.nethergrim.combogymdiary.googledrive;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

public abstract class BaseDriveActivity extends Activity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "BaseDriveActivity";

	public static final String KEY_AUTOBACKUP = "autobackup";

	protected static final String EXTRA_ACCOUNT_NAME = "account_name";

	protected static final int REQUEST_CODE_RESOLUTION = 1;

	protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onResume() {
		setContentView(R.layout.activity_base_drive);
		super.onResume();
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addScope(Drive.SCOPE_APPFOLDER)
					// required for App Folder sample
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onPause() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "GoogleApiClient connected");
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "GoogleApiClient connection suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			try {
				GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
						this, 0).show();
			} catch (Exception e) {
				Counter.sharedInstance().reportError("", e);
			}

			return;
		}
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}
	}

	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}
}