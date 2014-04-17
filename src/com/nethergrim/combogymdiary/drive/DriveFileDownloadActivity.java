package com.nethergrim.combogymdiary.drive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.nethergrim.combogymdiary.R;

public class DriveFileDownloadActivity extends BasicDriveActivity {

	private DriveId driveId;
	private String driveIdStr;
	private DriveFile file;
	private RestoreTask restoreTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk_restore);
		Intent intent = getIntent();
		driveIdStr = intent.getStringExtra(DRIVE_ID);
		driveId = DriveId.decodeFromString(driveIdStr);

	}
	
	@Override
	public void onBackPressed() {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		restoreTask = new RestoreTask();
		restoreTask.execute();
	}

	class RestoreTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			file = Drive.DriveApi.getFile(getGoogleApiClient(), driveId);

			ContentsResult contentResult = file.openContents(
					getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null)
					.await();
			if (contentResult.getStatus().isSuccess()) {
				Contents contents = contentResult.getContents();
				InputStream is = contents.getInputStream();
				File data = Environment.getDataDirectory();
				String currentDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
				File currentDB = new File(data, currentDBPath);
				OutputStream stream;
				try {
					stream = new BufferedOutputStream(new FileOutputStream(
							currentDB));

					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int len = 0;
					try {
						while ((len = is.read(buffer)) != -1) {
							stream.write(buffer, 0, len);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (stream != null)
						try {
							stream.close();

						} catch (IOException e) {
							e.printStackTrace();
						}

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			showMessage(getResources().getString(R.string.restored));
			finish();

		}
		
		
	}

}
