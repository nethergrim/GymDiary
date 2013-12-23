package com.nethergrim.combogymdiary;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {
	
  final String LOG_TAG = "myLogs";
  public static final String DB_NAME = "mydb";
  private static final int DB_VERSION = 3;
  
  private static final String DB_EXE_TABLE = "exe_tab";
  public static final String COLUMN_ID = "_id";
  public static final String EXE_NAME = "exercise_name";
  public static final String TRA_NAME = "training_name";
  public static final String TIMER_VALUE = "timer_value";
  
  public static final String DB_MAIN_TABLE = "main_tab";
  public static final String DATE = "Date";
  public static final String WEIGHT = "Weight";
  public static final String REPS = "Reps";
  public static final String SET = "SetsN";
  
  public static final String DB_MEASURE_TABLE = "measurements_tab";
  public static final String PART_OF_BODY_FOR_MEASURING = "part_of_body";
  public static final String MEASURE_VALUE = "measure_value";
  
  public static final String DB_TRAININGS_TABLE = "trainings_tab";
  
  
  private static final String DB_EXE_CREATE = 
    "create table " + DB_EXE_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, "+
    //		TRA_NAME+" text, " +
    		EXE_NAME+" text, "+ 
    		TIMER_VALUE + " text" +
    		");";
  
  private static final String DB_MAIN_CREATE = 
		    "create table " + DB_MAIN_TABLE + "(" +
		      COLUMN_ID + " integer primary key autoincrement, "+
		      TRA_NAME + " text, " +
		      EXE_NAME + " text, " + 
		      DATE + " text, " +
		      WEIGHT + " integer, " +
		      REPS + " integer, " +
		      SET + " integer" +
		      ");";
  
  private static final String DB_MEASURE_CREATE = 
		    "create table " + DB_MEASURE_TABLE + "(" +
		      COLUMN_ID + " integer primary key autoincrement, "+
		      DATE + " text, " +
		      PART_OF_BODY_FOR_MEASURING + " text, "+ 
		      MEASURE_VALUE + " text" +
		      ");";
  
  private static final String DB_TRAININGS_CREATE = 
		    "create table " + DB_TRAININGS_TABLE + "(" +
		      COLUMN_ID + " integer primary key autoincrement, "+
		    		TRA_NAME+" text, " +
		    		EXE_NAME+" text"+ 
		    		");";
  public static String strSeparator = "__,__";
  
  
  private Context mCtx;  
  private DBHelper mDBHelper;
  public SQLiteDatabase mDB;
  
  public DB(Context ctx) {
	    mCtx = ctx;
	  }
  
  
  
  public  String convertArrayToString(String[] array){
      String str = "";
      for (int i = 0;i<array.length; i++) {
          str = str+array[i];
          // Do not append comma at the end of last element
          if(i<array.length-1){
              str = str+strSeparator;
          }
      }
      return str;
  }
  public  String[] convertStringToArray(String str){
      String[] arr = str.split(strSeparator);
      return arr;
  }
  public void open() {
    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
    mDB = mDBHelper.getWritableDatabase();
  }
  
  public void close() {
    if (mDBHelper!=null) mDBHelper.close();
  }
  
  public Cursor getAllData_Exe() {
	       
	  return mDB.query(DB_EXE_TABLE, null, null, null, null, null, null);
  }
  
  public Cursor getData_Exe_GroupBy (String groupBy) {
      
	  return mDB.query(DB_EXE_TABLE, null, null, null, groupBy, null, null);
  }
  
  public Cursor getData_Main_GroupBy (String groupBy) {
      
	  return mDB.query(DB_MAIN_TABLE, null, null, null, groupBy, null, null);
  }  
  
  
  public int getLastReps(String _exeName, int _set) {
	  int result = 0;
      String[] cols = {DB.REPS,DB.SET};
      String[] tags = {_exeName};
	  Cursor c = mDB.query(DB_MAIN_TABLE, cols, DB.EXE_NAME+"=?", tags, null, null, null);
	  int size = c.getCount();
	  if (size > 1) {
		  int positionLastDay = size - _set - 1;
		  c.moveToPosition(positionLastDay);
		  _set++;
		  if (size > _set + 1) {
			  if (c.getInt(1) < _set ) { 
			  } else if (c.getInt(1) == _set){ 
			  		result =  c.getInt(0);
			  } else if ( c.getInt(1) > _set ) { 
				  while ( c.getInt(1) > _set ) {
					c.moveToPrevious();
					result =  c.getInt(0);
				  }
			  }
		  }		  
	  }	  
	  return result;
  }
  
  public int getLastWeight(String _exeName, int _set) {
      int result = 0;
      String[] cols = {DB.WEIGHT,DB.SET};
      String[] tags = {_exeName};      
      Cursor c = mDB.query(DB_MAIN_TABLE, cols, DB.EXE_NAME+"=?", tags, null, null, null);
	  int size = c.getCount();
	  if (size > 1) {
		  int positionLastDay = size - _set - 1;
		  c.moveToPosition(positionLastDay);
		  _set++;
		  if (size > _set + 1) {
			  if (c.getInt(1) < _set ) { 
			  } else if (c.getInt(1) == _set){ 
			  		result =  c.getInt(0);
			  } else if ( c.getInt(1) > _set ) { 
				  while ( c.getInt(1) > _set ) {
					c.moveToPrevious();
					result =  c.getInt(0);
				  	}
			  }
		  }		  
	  }	  
	  return result;
  }
  
  public String getTimerValueByExerciseName (String exeName) 
  {	  
	  String[] cols = {DB.TIMER_VALUE};
	  String[] tags = {exeName};
	  Cursor c1 = mDB.query(DB_EXE_TABLE, cols, DB.EXE_NAME+"=?", tags, null, null, null, null);
	  c1.moveToFirst();
	  String result = c1.getString(0);
	  Log.d(LOG_TAG, "Result of GetTimerValueByExercise: "+result);
	  return result;
  }
  
  public int getIdValueByExerciseName (String exeName) 
  {
	  int result;
	  String[] cols = {DB.COLUMN_ID};	  
	  String[] tags = {exeName};
	  Cursor c = mDB.query(DB_EXE_TABLE, cols, DB.EXE_NAME+"=?", tags, null, null, null, null);
	  c.moveToFirst();
	  result = c.getInt(0);
	  return result;
  }
  
  public Cursor getAllData_Main() {
	    return mDB.query(DB_MAIN_TABLE, null, null, null, null, null, null);
	  }
     
  public Cursor getDataMain
  			(String[] column, 		// The columns to return
		  String selection,			// The columns for the WHERE clause
		  String[] selectionArgs,	// The values for the WHERE clause
		  String groupBy,			// group the rows
		  String having,			// filter by row groups
		  String orderedBy			// The sort order
		  )
  {
	  
	  return mDB.query(DB_MAIN_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
  }
  
  public Cursor getDataExe
		(String[] column, 		// The columns to return
		String selection,			// The columns for the WHERE clause
		String[] selectionArgs,	// The values for the WHERE clause
		String groupBy,			// group the rows
		String having,			// filter by row groups
		String orderedBy			// The sort order
				)
{
return mDB.query(DB_EXE_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
}
 
  public Cursor getDataTrainings
	(String[] column, 		
	String selection,			
	String[] selectionArgs,
	String groupBy,			
	String having,			
	String orderedBy		
			)
{
return mDB.query(DB_TRAININGS_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
}
  
  public Cursor getDataMeasures
	(String[] column, 		// The columns to return
	String selection,			// The columns for the WHERE clause
	String[] selectionArgs,	// The values for the WHERE clause
	String groupBy,			// group the rows
	String having,			// filter by row groups
	String orderedBy			// The sort order
			)
	{
	return mDB.query(DB_MEASURE_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
}
  
  public void addRec_Exe(String exeName, String timer) {
    ContentValues cv = new ContentValues();
    cv.put(EXE_NAME, exeName);
    cv.put(TIMER_VALUE, timer);
    mDB.insert(DB_EXE_TABLE, null, cv);
  }
  
  public void addRec_Trainings(String traName ,String exeName) {
	    ContentValues cv = new ContentValues();
	    cv.put(EXE_NAME, exeName);
	    cv.put(TRA_NAME, traName);
	    mDB.insert(DB_TRAININGS_TABLE, null, cv);
	  }
  
  public void addRec_Measure(String date ,String part_of_body, String value) {
	    ContentValues cv = new ContentValues();
	    cv.put(DATE, date);
	    cv.put(PART_OF_BODY_FOR_MEASURING, part_of_body);
	    cv.put(MEASURE_VALUE, value);
	    mDB.insert(DB_MEASURE_TABLE, null, cv);
	    Log.d(LOG_TAG, "Added row: date = "+date+" part_of_body = "+part_of_body+" value = "+value);
	  }
  
  public void addRec_Main(String traName ,String exeName, String timer,String date, int weight, int reps,int set) {
	    ContentValues cv = new ContentValues();
	    cv.put(EXE_NAME, exeName);
	    cv.put(TRA_NAME, traName);
	    cv.put(DATE, date);
	    cv.put(WEIGHT, weight);
	    cv.put(REPS, reps);
	    cv.put(SET,set);
	    mDB.insert(DB_MAIN_TABLE, null, cv);
 }
  
  public void updateRec_Exe(int Id, String column, String data )  {
	  ContentValues cv1 = new ContentValues();
	  cv1.put(column, data);
	  Log.d(LOG_TAG, "===DB_exe  Editing column " + column + " at ID = " + Id + " with data: " + data);
	  mDB.update(DB_EXE_TABLE, cv1, "_id = " + Id, null);
  }
  
  public void delDB(){
	  mCtx.deleteDatabase(DB.DB_NAME);
  }
  
  public void updateRec_Main(int Id, int colId, String data_str, int data_int )  {
	  ContentValues cv = new ContentValues();
	  if (colId == 1)   {
		  cv.put(TRA_NAME ,data_str);		  
	  }  else if (colId == 2) 	  {
		  cv.put(EXE_NAME,data_str);		  
	  }  else if (colId == 3) 	  {
		  cv.put(TIMER_VALUE, data_str);		  
	  }  else if(colId == 4) 	  {
		  cv.put(DATE, data_str);
	  }  else if (colId == 5) 	  {
		  cv.put(WEIGHT, data_int);
	  } else if (colId == 6)	  {
		  cv.put(REPS, data_int);
	  } else if (colId == 7){
		  cv.put(SET, data_int);
	  }
	  mDB.update(DB_MAIN_TABLE, cv, "_id = " + Id, null);
  }
  
  public void delRec_Exe(long id) {
	  mDB.delete(DB_EXE_TABLE, COLUMN_ID + " = " + id, null);
  }
  
  public void delRec_Trainings(long id) {
	  mDB.delete(DB_TRAININGS_TABLE, COLUMN_ID + " = " + id, null);
  }
  
  public void delRec_Main(long id) {
	  mDB.delete(DB_MAIN_TABLE, COLUMN_ID + " = " + id, null);
	  }
  
  private class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, CursorFactory factory,
        int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d(LOG_TAG, "DB created");
    	db.execSQL(DB_EXE_CREATE);   
    	db.execSQL(DB_MAIN_CREATE);
    	db.execSQL(DB_MEASURE_CREATE);
    	db.execSQL(DB_TRAININGS_CREATE);
    	
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if ( oldVersion == 1 && newVersion == 2) {
    		Log.d(LOG_TAG, "DB updated from v1 to v2");
    		db.execSQL(DB_MEASURE_CREATE);
    	} else if ( oldVersion == 2 && newVersion == 3){
    		Log.d(LOG_TAG, "DB updating from v2 to v3");
    		db.beginTransaction();
    		
    		try {
    			db.execSQL(DB_TRAININGS_CREATE);
    			Cursor c = db.query(DB_EXE_TABLE, null, null, null, TRA_NAME, null, null);
    			if (c.moveToFirst()){	// ������ ������ ���������� �� ������� DB_EXE_TABLE, � ������������ � ������� DB_TRAININGS_TABLE, ��� ���������� ������������ ����� ��������.
    				do {
    					String[] args = {c.getString(1)};
    					Cursor cur_local = db.query(DB_EXE_TABLE, null, TRA_NAME + "=?", args, null, null, null);
    					if (cur_local.moveToFirst()){
    						String[] exercices = new String[cur_local.getCount()];
    						int i = 0;
    						do {
    							exercices[i] = cur_local.getString(2);
    							i++;
    						} while(cur_local.moveToNext());
    						String exes = convertArrayToString(exercices);
    						addRec_Trainings(c.getString(1), exes);
    					}
    				}while (c.moveToNext());
    			}
    			
    			
    			
    			db.execSQL("create temporary table exe_tmp (_id integer, exercise_name text, timer_value text);");
    			db.execSQL("insert into exe_tmp select _id, exercise_name, timer_value from exe_tab;");
    			db.execSQL("drop table exe_tab");
    			db.execSQL(DB_EXE_CREATE);
    			db.execSQL("insert into exe_tab select _id, exercise_name, timer_value from exe_tmp;");
    			db.execSQL("drop table exe_tmp;");

    	          db.setTransactionSuccessful();
    	        } finally {
    	          db.endTransaction();
    	        }
    	}
    }
  }
}
