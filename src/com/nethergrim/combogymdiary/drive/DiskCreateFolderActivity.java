package com.nethergrim.combogymdiary.drive;

import java.util.ArrayList;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveFolder.OnChildrenRetrievedCallback;
import com.google.android.gms.drive.DriveFolder.OnCreateFolderCallback;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
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
	private Boolean toCreateFolder = false;
	private MetadataBuffer mdb;

	private static Query[] sQueries = new Query[] {

	new Query.Builder().addFilter(
			Filters.and(Filters.eq(SearchableField.MIME_TYPE,
					"application/vnd.google-apps.folder"), Filters.eq(
					SearchableField.TITLE, DRIVE_FOLDER_NAME), Filters.eq(
					SearchableField.TRASHED, false))).build(), };

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

		if (sp.contains(BasicMenuActivity.DRIVE_EXISTS)
				&& sp.getBoolean(BasicMenuActivity.DRIVE_EXISTS, false)) {
			toCreateFolder = false; // do NOT create a new folder
		} else {
			toCreateFolder = true; // create a new folder at Google Drive
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		if (toCreateFolder) {
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(DRIVE_FOLDER_NAME).build();
			Drive.DriveApi.getRootFolder(getGoogleApiClient())
					.createFolder(getGoogleApiClient(), changeSet)
					.addResultCallback(this);
		} else {
			Drive.DriveApi.query(getGoogleApiClient(), sQueries[0])
					.addResultCallback(this);
		}
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
		sp.edit().putBoolean(BasicMenuActivity.DRIVE_EXISTS, true).apply();
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
		if (mdb.getCount() < 1) {
			sp.edit().putBoolean(DRIVE_EXISTS, false).apply();
			sp.edit().putString(DRIVE_FOLDER_ID_ENCODED_TO_STRING, "").apply();
			showMessage("re-creating a folder in Google Drive");
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(DRIVE_FOLDER_NAME).build();
			Drive.DriveApi.getRootFolder(getGoogleApiClient())
					.createFolder(getGoogleApiClient(), changeSet)
					.addResultCallback(this);

		} else {
			
			ArrayList<Metadata> mdArray = new ArrayList<Metadata>();
			for (int i = 0; i < mdb.getCount(); i++) {
				mdArray.add(mdb.get(i));
			}
			if (mdb.getCount() > 0){
				String newDriveId = mdArray.get(0).getDriveId().encodeToString();
				sp.edit().putString(DRIVE_FOLDER_ID_ENCODED_TO_STRING, newDriveId).apply();
			}
			
			gotoAutoBackup();
		}
	}

}
