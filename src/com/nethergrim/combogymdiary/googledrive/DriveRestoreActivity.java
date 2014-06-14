package com.nethergrim.combogymdiary.googledrive;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DriveRestoreActivity extends BaseDriveActivity {

    private static final int REQUEST_CODE_OPENER = 5;
    private static boolean blocked = false;
    private DriveId databaseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drive);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        if (!blocked) {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"text/plain"})
                    .build(getGoogleApiClient());
            try {
                startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER,
                        null, 0, 0, 0);
            } catch (SendIntentException e) {
                Counter.sharedInstance().reportError("", e);
            }
        }
        if (blocked) {
            RestoreTask task = new RestoreTask();
            task.execute();
            blocked = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data
                            .getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    databaseFile = driveId;
                    blocked = true;
                    getGoogleApiClient().disconnect();
                    getGoogleApiClient().connect();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getFile() {
        DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(),
                databaseFile);

        ContentsResult contentResult = file.openContents(getGoogleApiClient(),
                DriveFile.MODE_READ_ONLY, null).await();
        if (contentResult.getStatus().isSuccess()) {
            Contents contents = contentResult.getContents();
            InputStream is = contents.getInputStream();
            File data = Environment.getDataDirectory();
            File currentDB = new File(data,
                    "//data//com.nethergrim.combogymdiary//databases//mydb");
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
                    Counter.sharedInstance().reportError("", e);
                }
                if (stream != null)
                    try {
                        stream.close();

                    } catch (IOException e) {
                        Counter.sharedInstance().reportError("", e);
                    }

            } catch (FileNotFoundException e1) {
                Counter.sharedInstance().reportError("", e1);
            }
        }

    }

    @Override
    public void onBackPressed() {
    }

    class RestoreTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getFile();
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
