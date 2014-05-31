package com.nethergrim.combogymdiary.googledrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
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
public class DriveBackupActivity extends BaseDriveActivity {

	static final String DRIVE_FOLDER_NAME = "Workout Diary Backups";
	protected static final int REQUEST_CODE_CREATOR = 3;
	static DriveId FOLDER_DRIVE_ID;
	private static boolean isBackupManual = false;
	private String fileTitle;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_drive);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		fileTitle = "Trainings backup " + date + " .db";
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		Query query = new Query.Builder()
				.addFilter(
						Filters.eq(SearchableField.MIME_TYPE,
								"application/vnd.google-apps.folder"))
				.addFilter(Filters.eq(SearchableField.TITLE, DRIVE_FOLDER_NAME))
				.addFilter(Filters.eq(SearchableField.TRASHED, false)).build();

		// search for a folder named "Workout Diary Backups" in Google Drive
		Drive.DriveApi.query(getGoogleApiClient(), query).setResultCallback(
				folderQueryCallback);
	}

	final private ResultCallback<MetadataBufferResult> folderQueryCallback = new ResultCallback<MetadataBufferResult>() {
		@Override
		public void onResult(MetadataBufferResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Problem while retrieving results");
				return;
			}
			MetadataBuffer mdb = result.getMetadataBuffer();
			if (mdb.getCount() == 0) {
				// create a new folder
				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
						.setTitle(DRIVE_FOLDER_NAME).build();
				Drive.DriveApi.getRootFolder(getGoogleApiClient())
						.createFolder(getGoogleApiClient(), changeSet)
						.setResultCallback(folderCreatedCallback);
			} else {
				// get folder DriveID and proceed
				FOLDER_DRIVE_ID = mdb.get(0).getDriveId();
				goIntoFolder();
			}
		}
	};

	final private ResultCallback<DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolderResult>() {
		@Override
		public void onResult(DriveFolderResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Error while trying to create the folder");
				return;
			}
			// new folder DriveID
			FOLDER_DRIVE_ID = result.getDriveFolder().getDriveId();
			goIntoFolder();
		}
	};

	private void goIntoFolder() {
		// here we already have DriveId for a folder
		// next step is backup DB file to a folder
		Bundle b = getIntent().getExtras();
		isBackupManual = !b.getBoolean(KEY_AUTOBACKUP);
		if (isBackupManual) {
			// manual backup using Creator Activity
			Drive.DriveApi.newContents(getGoogleApiClient()).setResultCallback(
					contentsManualFileCreateCallback);
		} else {
			// automatic backup
			Drive.DriveApi.newContents(getGoogleApiClient()).setResultCallback(
					newContentsResultOnCreatingFile);
		}
	}

	final ResultCallback<ContentsResult> contentsManualFileCreateCallback = new ResultCallback<ContentsResult>() {
		@Override
		public void onResult(ContentsResult result) {
			if (!result.getStatus().isSuccess()) {
				return;
			}

			OutputStream outputStream = result.getContents().getOutputStream();
			Backuper back = new Backuper();
			File db = back.getDbFile();
			byte[] b = new byte[(int) db.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(db);
				fileInputStream.read(b);
				for (int i = 0; i < b.length; i++) {
					System.out.print((char) b[i]);
				}
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				Counter.sharedInstance().reportError("", e);
			} catch (IOException e1) {
				Counter.sharedInstance().reportError("", e1);
			}
			try {
				outputStream.write(b);
			} catch (IOException e1) {
				Counter.sharedInstance().reportError("", e1);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			String date = sdf.format(new Date(System.currentTimeMillis()));
			String fileTitle = "Trainings backup " + date + " .db";

			MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
					.setMimeType("text/plain").setTitle(fileTitle).build();
			IntentSender intentSender = Drive.DriveApi
					.newCreateFileActivityBuilder()
					.setActivityStartFolder(FOLDER_DRIVE_ID)
					.setInitialMetadata(metadataChangeSet)
					.setInitialContents(result.getContents())
					.build(getGoogleApiClient());
			try {
				startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR,
						null, 0, 0, 0);
			} catch (SendIntentException e) {
				Counter.sharedInstance().reportError("", e);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CREATOR:
			finish();
			showMessage(getResources().getString(R.string.backuped));
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	final private ResultCallback<ContentsResult> newContentsResultOnCreatingFile = new ResultCallback<ContentsResult>() {
		@Override
		public void onResult(ContentsResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Error while trying to create new file contents");
				return;
			}

			OutputStream outputStream = result.getContents().getOutputStream();
			Backuper back = new Backuper();
			File db = back.getDbFile();

			byte[] b = new byte[(int) db.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(db);
				fileInputStream.read(b);
				for (int i = 0; i < b.length; i++) {
					System.out.print((char) b[i]);
				}
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				Counter.sharedInstance().reportError("", e);
			} catch (IOException e1) {
				Counter.sharedInstance().reportError("", e1);
			}

			try {
				outputStream.write(b);
			} catch (IOException e1) {
				Counter.sharedInstance().reportError("", e1);
			}

			DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(),
					FOLDER_DRIVE_ID);
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(fileTitle).setMimeType("text/plain")
					.setStarred(true).build();
			folder.createFile(getGoogleApiClient(), changeSet,
					result.getContents())
					.setResultCallback(fileCreatedCallback);
		}
	};

	final private ResultCallback<DriveFileResult> fileCreatedCallback = new ResultCallback<DriveFileResult>() {
		@Override
		public void onResult(DriveFileResult result) {
			if (!result.getStatus().isSuccess()) {
				showMessage("Error while trying to create the file");
				return;
			} else {
				showMessage(getResources().getString(
						R.string.drive_backuped_true));
			}
			finish();
		}
	};
}
