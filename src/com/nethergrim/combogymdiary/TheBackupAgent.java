package com.nethergrim.combogymdiary;

import java.io.File;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;


public class TheBackupAgent extends BackupAgentHelper {
  /* // The name of the SharedPreferences file
   static final String DB_FILENAME = "//data//com.nethergrim.combogymdiary//databases//mydb";

   // A key to uniquely identify the set of backup data
   static final String FILES_BACKUP_KEY = "combogymdiary_DB";

   // Allocate a helper and add it to the backup agent
   @Override
   public  void onCreate() {
       FileBackupHelper helper = new FileBackupHelper(this, DB_FILENAME);
       addHelper(FILES_BACKUP_KEY, helper);
   }*/
   
   
   
   private static final String DB_NAME = "mydb";

   static final String FILES_BACKUP_KEY = "combogymdiary_DB";
   
   @Override
   public void onCreate(){
      FileBackupHelper helper = new FileBackupHelper(this, DB_NAME);
      addHelper(FILES_BACKUP_KEY, helper);
   }

   @Override
   public File getFilesDir(){
      File path = getDatabasePath(DB_NAME);
      return path.getParentFile();
   }
}
