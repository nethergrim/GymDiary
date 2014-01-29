package com.nethergrim.combogymdiary;

import java.io.File;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class TheBackupAgent extends BackupAgentHelper {

	private static final String DB_NAME = "mydb";

	static final String FILES_BACKUP_KEY = "combogymdiary_DB";

	@Override
	public void onCreate() {
		FileBackupHelper helper = new FileBackupHelper(this, DB_NAME);
		addHelper(FILES_BACKUP_KEY, helper);
	}

	@Override
	public File getFilesDir() {
		File path = getDatabasePath(DB_NAME);
		return path.getParentFile();
	}
}
