package com.nethergrim.combogymdiary;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveFolder.OnCreateFolderCallback;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DiskCreateFolderActivity extends BaseDiskActivity implements
		OnCreateFolderCallback {

	private SharedPreferences sp;
	private Boolean toCreateFolder = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk_restore);

	}

	@Override
	public void onResume() {
		super.onResume();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (sp.contains(BasicMenuActivity.DRIVE_EXISTS)
				&& sp.getBoolean(BasicMenuActivity.DRIVE_EXISTS, false)) {
			toCreateFolder = false;
			// do NOT create a new folder
			Intent intent = new Intent (this,DiskAutoBackupActivity.class);
			startActivity(intent);
			finish();
		} else {
			// create a new folder at Google Drive
			toCreateFolder = true;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		if (toCreateFolder) {
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle("Workout Diary Backups").build();
			Drive.DriveApi.getRootFolder(getGoogleApiClient())
					.createFolder(getGoogleApiClient(), changeSet)
					.addResultCallback(this);
		} else {

		}

	}

	@Override
	public void onCreateFolder(DriveFolderResult result) {

		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create the folder");
			return;
		}

		DriveId FolderDriveId = result.getDriveFolder().getDriveId();
		String FolderDriveIdStr = FolderDriveId.encodeToString();
		sp.edit().putBoolean(BasicMenuActivity.DRIVE_EXISTS, true).apply();
		sp.edit()
				.putString(BasicMenuActivity.DRIVE_FOLDER_ID_ENCODED_TO_STRING,
						FolderDriveIdStr).apply();
		Intent intent = new Intent (this,DiskAutoBackupActivity.class);
		startActivity(intent);
		finish();

	}

}
