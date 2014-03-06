package com.nethergrim.combogymdiary.drive;

import com.google.android.gms.common.api.GoogleApiClient;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.nethergrim.combogymdiary.R;

public class DiskRestoreActivity extends BaseDiskActivity {

	private static final int REQUEST_CODE_OPENER = 1;
	private String LOG_TAG = "drive";
	private GoogleApiClient client = null;
	private DriveId driveId;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_disk_restore);

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		client = getGoogleApiClient();
		IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
				.setMimeType(new String[] { "text/plain" }).build(client);
		try {
			startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null,
					0, 0, 0);
		} catch (SendIntentException e) {
			Log.w(LOG_TAG, "Unable to send intent", e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				driveId = (DriveId) data
						.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				if (driveId != null) {
					gotoDownloadActivity(driveId);
				} else {
					finish();
				}

			}
			finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void gotoDownloadActivity(DriveId driveId) {
		Intent intent = new Intent(this, DiskFileDownloadActivity.class);
		intent.putExtra(DRIVE_ID, driveId.encodeToString());
		startActivity(intent);
	}

}