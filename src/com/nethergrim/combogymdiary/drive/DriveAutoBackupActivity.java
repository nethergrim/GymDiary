package com.nethergrim.combogymdiary.drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.OnNewContentsCallback;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.OnCreateFileCallback;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.nethergrim.combogymdiary.Backuper;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;

@SuppressLint("SimpleDateFormat")
public class DriveAutoBackupActivity extends BaseDriveActivity implements
		OnNewContentsCallback, OnCreateFileCallback {

	private SharedPreferences sp;
	private String folderDriveIdStr;
	private String fileTitle;
	private DriveId folderDriveId;
	private File db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk_restore);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		fileTitle = "Trainings backup " + date + " .db";
	}

	@Override
	protected void onResume() {
		super.onResume();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		folderDriveIdStr = sp.getString(DRIVE_FOLDER_ID_ENCODED_TO_STRING, "");
		if (folderDriveIdStr.isEmpty()) {
			finish();
			return;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		Drive.DriveApi.newContents(getGoogleApiClient())
				.addResultCallback(this);
	}

	@Override
	public void onCreateFile(DriveFileResult result) {
		if (!result.getStatus().isSuccess()) {
			showMessage("Error while trying to create the file");
			return;
		}
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.drive_backuped_true),
				Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onNewContents(ContentsResult result) {
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

		try {
			folderDriveId = DriveId.decodeFromString(folderDriveIdStr);
			DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(),
					folderDriveId);
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(fileTitle).setMimeType("text/plain")
					.setStarred(true).build();

			folder.createFile(getGoogleApiClient(), changeSet,
					result.getContents()).addResultCallback(this);
		} catch (Exception e) {
			Counter.sharedInstance().reportError(
					"disk auto backup activity fail at creation file", e);
		}

	}

}
