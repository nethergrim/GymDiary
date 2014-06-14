package com.nethergrim.combogymdiary;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Backuper {
    final String LOG_TAG = "myLogs";

    @SuppressWarnings("resource")
    public boolean backupToSd() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
                String backupDBPath = "Workout_diary_backup.db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    Log.d(LOG_TAG, "DB for backup exists");
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return true;
                } else
                    return false;
            } else {
                Log.d(LOG_TAG, "sd.canWrite == 0");
                return false;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "" + e);
            return false;
        }
    }

    @SuppressWarnings("resource")
    public boolean restoreBackup(String path) {
        Log.d(LOG_TAG, "backuping path " + path);
        try {
            File sd1 = Environment.getExternalStorageDirectory();
            Log.d(LOG_TAG, "sd1 path: " + sd1.getAbsolutePath());
            File data = Environment.getDataDirectory();
            if (sd1.canWrite()) {
                String restroredDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
                File restoredDB = new File(data, restroredDBPath);
                File backupedDB = new File(path);
                if (backupedDB.exists()) {
                    FileChannel src = new FileInputStream(backupedDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(restoredDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Log.d(LOG_TAG, "restored OK");
                    return true;
                } else {
                    Log.d(LOG_TAG, "backupedDB.exists() == false");
                    return false;
                }
            } else
                Log.d(LOG_TAG, "sd.canWrite  == false");
            return false;
        } catch (Exception e) {
            Log.d(LOG_TAG, "error restoring DB: " + e);
            return false;
        }

    }

    public File getDbFile() {
        File data = Environment.getDataDirectory();
        String currentDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
        return new File(data, currentDBPath);

    }
}
