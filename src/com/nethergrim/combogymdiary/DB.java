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
  private static final String DB_NAME = "mydb";
  private static final int DB_VERSION = 1;
  
  private static final String DB_EXE_TABLE = "exe_tab";// ��������� / ���������� / ������
  public static final String COLUMN_ID = "_id";
  public static final String EXE_NAME = "exercise_name";
  public static final String TRA_NAME = "training_name";
  public static final String TIMER_VALUE = "timer_value";
  
  public static final String DB_MAIN_TABLE = "main_tab";// ������� �������
  public static final String DATE = "Date";
  public static final String WEIGHT = "Weight";
  public static final String REPS = "Reps";
  public static final String SET = "SetsN";
  

  private static final String DB_EXE_CREATE = 
    "create table " + DB_EXE_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, "+
    		TRA_NAME+" text, " +
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
  
  
  private Context mCtx;  
  private DBHelper mDBHelper;
  public SQLiteDatabase mDB;
  
  public DB(Context ctx) {
	    mCtx = ctx;
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
  
  public void addRec_Exe(String traName ,String exeName, String timer) {
    ContentValues cv = new ContentValues();
    cv.put(EXE_NAME, exeName);
    cv.put(TRA_NAME, traName);
    cv.put(TIMER_VALUE, timer);
    mDB.insert(DB_EXE_TABLE, null, cv);
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
	  Log.d(LOG_TAG, "--- Update mytabe: ---");
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
	  int delCount = mDB.delete(DB_EXE_TABLE, COLUMN_ID + " = " + id, null);
	  Log.d(LOG_TAG, "deleted rows count = " + delCount);
  }
  
  public void delRec_Main(long id) {
	  int delCount = mDB.delete(DB_MAIN_TABLE, COLUMN_ID + " = " + id, null);
	  Log.d(LOG_TAG, "deleted rows count = " + delCount);
	  }
  
  private class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, CursorFactory factory,
        int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d(LOG_TAG, "--- onCreate database ---");
    	db.execSQL(DB_EXE_CREATE);   
    	db.execSQL(DB_MAIN_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
  }
}
