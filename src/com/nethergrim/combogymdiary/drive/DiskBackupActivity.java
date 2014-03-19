package com.nethergrim.combogymdiary.drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.OnNewContentsCallback;
import com.google.android.gms.drive.MetadataChangeSet;
import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

public class DiskBackupActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = "android-drive-quickstart";
	private static final int REQUEST_CODE_CREATOR = 2;
	private static final int REQUEST_CODE_RESOLUTION = 3;

	private GoogleApiClient mGoogleApiClient;
	private File db;

	private void saveFileToDrive() {
		// Start by creating a new contents, and setting a callback.
		Log.i(TAG, "Creating new contents.");

		Drive.DriveApi.newContents(mGoogleApiClient).addResultCallback(
				new OnNewContentsCallback() {

					@Override
					public void onNewContents(ContentsResult result) {
						// If the operation was not successful, we cannot do
						// anything
						// and must
						// fail.
						if (!result.getStatus().isSuccess()) {
							Log.i(TAG, "Failed to create new contents.");
							return;
						}
						// Otherwise, we can write our data to the new contents.
						Log.i(TAG, "New contents created.");
						// Get an output stream for the contents.
						OutputStream outputStream = result.getContents()
								.getOutputStream();
						// Write the bitmap data from it.
						Backuper back = new Backuper();

						db = back.getDbFile();
						byte[] b = new byte[(int) db.length()];
						try {
							FileInputStream fileInputStream = new FileInputStream(
									db);
							fileInputStream.read(b);
							for (int i = 0; i < b.length; i++) {
								System.out.print((char) b[i]);
							}
							fileInputStream.close();
						} catch (FileNotFoundException e) {
							System.out.println("File Not Found.");
							e.printStackTrace();
						} catch (IOException e1) {
							System.out.println("Error Reading The File.");
							e1.printStackTrace();
						}

						try {
							outputStream.write(b);
						} catch (IOException e1) {
							Log.i(TAG, "Unable to write file contents.");
						}
						// Create the initial metadata - MIME type and title.
						// Note that the user will be able to change the title
						// later.
						SimpleDateFormat sdf = new SimpleDateFormat(
								"dd.MM.yyyy");
						String date = sdf.format(new Date(System
								.currentTimeMillis()));
						String fileTitle = "Trainings backup " + date + " .db";
						MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
								.setMimeType("text/plain").setTitle(fileTitle)
								.build();
						// Create an intent for the file chooser, and start it.

						try {
							IntentSender intentSender = Drive.DriveApi
									.newCreateFileActivityBuilder()
									.setInitialMetadata(metadataChangeSet)
									.setInitialContents(result.getContents())
									.build(mGoogleApiClient);
							try {
								startIntentSenderForResult(intentSender,
										REQUEST_CODE_CREATOR, null, 0, 0, 0);
							} catch (SendIntentException e) {
								Log.i(TAG, "Failed to launch file chooser.");
							}
						} catch (Exception e) {

							Counter.sharedInstance().reportError(
									"ERROR at DiskBackupActivity", e);
							mGoogleApiClient.connect();

							IntentSender intentSender = Drive.DriveApi
									.newCreateFileActivityBuilder()
									.setInitialMetadata(metadataChangeSet)
									.setInitialContents(result.getContents())
									.build(mGoogleApiClient);
							try {
								startIntentSenderForResult(intentSender,
										REQUEST_CODE_CREATOR, null, 0, 0, 0);
							} catch (SendIntentException EE) {
								Log.i(TAG, "Failed to launch file chooser.");
							}

						}

					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient == null) {
			// Create the API client and bind it to an instance variable.
			// We use this instance as the callback for connection and
			// connection
			// failures.
			// Since no account name is passed, the user is prompted to choose.
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		// Connect the client. Once connected, the camera is launched.
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {

		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CREATOR:
			// Called after a file is saved to Drive.
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), R.string.saved,
						Toast.LENGTH_SHORT).show();
				Log.i(TAG, "File successfully saved.");

			}
			finish();
			break;

		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Called whenever the API client fails to connect.
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		// The failure has a resolution. Resolve it.
		// Called typically when the app is not yet authorized, and an
		// authorization
		// dialog is displayed to the user.
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "API client connected.");
		/*
		 * if (mBitmapToSave == null) { // This activity has no UI of its own.
		 * Just start the camera. startActivityForResult(new
		 * Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE);
		 * return; }
		 */
		saveFileToDrive();
	}

	@Override
	public void onDisconnected() {
		Log.i(TAG, "API client disconnected.");
	}

}