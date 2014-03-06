package com.nethergrim.combogymdiary.drive;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveFolder.OnChildrenRetrievedCallback;
import com.google.android.gms.drive.DriveFolder.OnCreateFolderCallback;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BasicMenuActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class DiskCreateFolderActivity extends BaseDiskActivity implements
		OnCreateFolderCallback, OnChildrenRetrievedCallback {

	private SharedPreferences sp;
	private MetadataBuffer mdb;

	private static Query[] sQueries = new Query[] { new Query.Builder()
			.addFilter(
					Filters.and(Filters.eq(SearchableField.MIME_TYPE,
							"application/vnd.google-apps.folder"), Filters.eq(
							SearchableField.TITLE, DRIVE_FOLDER_NAME), Filters
							.eq(SearchableField.TRASHED, false))).build(), };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk_restore);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		Drive.DriveApi.query(getGoogleApiClient(), sQueries[0])
				.addResultCallback(this);
	}

	private void gotoAutoBackup() {
		Intent intent = new Intent(this, DiskAutoBackupActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCreateFolder(DriveFolderResult result) {

		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create the folder");
			return;
		} else
			Log.d(TAG, "folder created");

		DriveId FolderDriveId = result.getDriveFolder().getDriveId();
		String FolderDriveIdStr = FolderDriveId.encodeToString();
		sp.edit()
				.putString(BasicMenuActivity.DRIVE_FOLDER_ID_ENCODED_TO_STRING,
						FolderDriveIdStr).apply();
		gotoAutoBackup();
	}

	@Override
	public void onChildrenRetrieved(MetadataBufferResult result) {
		if (!result.getStatus().isSuccess()) {
			return;
		}
		mdb = result.getMetadataBuffer();
		if (mdb.getCount() == 0) { // create a folder and than upload a file
									// there
			sp.edit().putBoolean(DRIVE_EXISTS, false).apply();
			showMessage("creating a folder in Google Drive");
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(DRIVE_FOLDER_NAME).build();
			Drive.DriveApi.getRootFolder(getGoogleApiClient())
					.createFolder(getGoogleApiClient(), changeSet)
					.addResultCallback(this);
			sp.edit().putBoolean(DRIVE_EXISTS, true).apply();

		} else if (mdb.getCount() > 0) { // just get Folder DriveId and upload a file there
			sp.edit()
					.putString(DRIVE_FOLDER_ID_ENCODED_TO_STRING,
							mdb.get(0).getDriveId().encodeToString()).apply();
			gotoAutoBackup();
		}
	}

}