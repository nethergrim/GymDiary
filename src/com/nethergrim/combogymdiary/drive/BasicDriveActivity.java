package com.nethergrim.combogymdiary.drive;

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
import com.yandex.metrica.Counter;

public abstract class BasicDriveActivity extends Activity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	protected static final String TAG = "BaseDriveActivity";
	public final static String DRIVE_EXISTS = "drive_exists";
	public final static String DRIVE_FOLDER_ID_ENCODED_TO_STRING = "drive_folder_id";
	protected static final String DRIVE_FOLDER_NAME = "Workout Diary Backups";
	protected static final String DRIVE_ID = "drive_id";

	/**
	 * Extra for account name.
	 */
	protected static final String EXTRA_ACCOUNT_NAME = "account_name";

	/**
	 * Request code for auto Google Play Services error resolution.
	 */
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	/**
	 * Next available request code.
	 */
	protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

	/**
	 * Google API client.
	 */
	private GoogleApiClient mGoogleApiClient;

	/**
	 * Called when activity gets visible. A connection to Drive services need to
	 * be initiated as soon as the activity is visible. Registers
	 * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
	 * activities itself.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();
		Counter.sharedInstance().onResumeActivity(this);
	}

	/**
	 * Handles resolution callbacks.
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
			mGoogleApiClient.connect();
		}
	}

	/**
	 * Called when activity gets invisible. Connection to Drive service needs to
	 * be disconnected as soon as an activity is invisible.
	 */
	@Override
	protected void onPause() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
		Counter.sharedInstance().onPauseActivity(this);
	}

	/**
	 * Called when {@code mGoogleApiClient} is connected.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "GoogleApiClient connected");
	}

	/**
	 * Called when {@code mGoogleApiClient} is disconnected.
	 */
	@Override
	public void onDisconnected() {
		Log.i(TAG, "GoogleApiClient disconnected");
		getGoogleApiClient().connect();
	}

	/**
	 * Called when {@code mGoogleApiClient} is trying to connect but failed.
	 * Handle {@code result.getResolution()} if there is a resolution is
	 * available.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}
	}

	/**
	 * Shows a toast message.
	 */
	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Getter for the {@code GoogleApiClient}.
	 */
	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}
}