package com.nethergrim.combogymdiary.drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveApi.OnNewContentsCallback;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveFolder.OnChildrenRetrievedCallback;
import com.google.android.gms.drive.DriveFolder.OnCreateFileCallback;
import com.google.android.gms.drive.DriveFolder.OnCreateFolderCallback;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

@SuppressLint("SimpleDateFormat")
public class DriveAutoBackupService extends BasicDriveService implements
		OnNewContentsCallback, OnCreateFileCallback, OnCreateFolderCallback,
		OnChildrenRetrievedCallback {

	private String fileTitle;
	private DriveId folderDriveId;
	private File db;
	private int ID;
	private MetadataBuffer mdb;
	private static Query[] sQueries = new Query[] { new Query.Builder()
			.addFilter(
					Filters.and(Filters.eq(SearchableField.MIME_TYPE,
							"application/vnd.google-apps.folder"), Filters.eq(
							SearchableField.TITLE, DRIVE_FOLDER_NAME), Filters
							.eq(SearchableField.TRASHED, false))).build(), };

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		ID = startId;
		String date = sdf.format(new Date(System.currentTimeMillis()));
		fileTitle = "Trainings backup " + date + " .db";

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		Log.d(TAG, "onConnected");
		Drive.DriveApi.query(getGoogleApiClient(), sQueries[0])
				.addResultCallback(this);

		Drive.DriveApi.newContents(getGoogleApiClient())
				.addResultCallback(this);
	}

	@Override
	public void onCreateFile(DriveFileResult result) {
		Log.d(TAG, "onCreateFile");
		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create the file");
			return;
		}
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.drive_backuped_true),
				Toast.LENGTH_LONG).show();
		Counter.sharedInstance().reportEvent("Backuped to Google Drive");
		disconnect();
		stopSelf(ID);
	}

	@Override
	public void onNewContents(ContentsResult result) {
		Log.d(TAG, "onNewContents");
		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create new file contents");
			return;
		}

		OutputStream outputStream = result.getContents().getOutputStream();

		Backuper back = new Backuper();
		db = back.getDbFile();

		byte[] b = new byte[(int) db.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(db);
			fileInputStream.read(b);
			for (int i = 0; i < b.length; i++) {
				System.out.print((char) b[i]);
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			outputStream.write(b);
		} catch (IOException e1) {
			Log.i(TAG, "Unable to write file contents.");
		}

		DriveFolder folder;
		try{
			folder = Drive.DriveApi.getFolder(getGoogleApiClient(),
					folderDriveId);
		}catch(Exception e){
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			folder = Drive.DriveApi.getFolder(getGoogleApiClient(),DriveId.decodeFromString(sp.getString(DRIVE_FOLDER_ID_ENCODED_TO_STRING, "")) );
			Counter.sharedInstance().reportError("folder = Drive.DriveApi.getFolder(getGoogleApiClient(),folderDriveId); FAILED, not found ID", e);
		}
		
		MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
				.setTitle(fileTitle).setMimeType("text/plain").setStarred(true)
				.build();

		folder.createFile(getGoogleApiClient(), changeSet, result.getContents())
				.addResultCallback(this);
	}

	@Override
	public void onChildrenRetrieved(MetadataBufferResult result) {
		Log.d(TAG, "onChildrenRetrieved");
		if (!result.getStatus().isSuccess()) {
			return;
		}
		mdb = result.getMetadataBuffer();
		if (mdb.getCount() == 0) { // create a folder and than upload a file
									// there
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(DRIVE_FOLDER_NAME).build();
			Drive.DriveApi.getRootFolder(getGoogleApiClient())
					.createFolder(getGoogleApiClient(), changeSet)
					.addResultCallback(this);

		} else if (mdb.getCount() > 0) { // just get Folder DriveId and upload a
											// file there
			folderDriveId = mdb.get(0).getDriveId();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			sp.edit().putString(DRIVE_FOLDER_ID_ENCODED_TO_STRING, folderDriveId.encodeToString()).apply();
		}
	}

	@Override
	public void onCreateFolder(DriveFolderResult result) {
		Log.d(TAG, "onCreateFolder");
		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create the folder");
			return;
		} else
			Log.d(TAG, "drive folder created");

		folderDriveId = result.getDriveFolder().getDriveId();
	}

}
