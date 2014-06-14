package com.nethergrim.combogymdiary;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class Statistics {

    private static final String LOG_TAG = "myLogs";
    private static DB db = null;
    private static Context context = null;

    private static void init(Context con) {
        context = con;
        db = new DB(context);
        db.open();
    }

    private static void close() {
        if (db != null) {
            db.close();
        }
    }

    public static String getMainExercise(Context _context) {
        init(_context);
        String result = context.getResources().getString(R.string.none);
        Cursor exercises = db.getDataMain(DB.EXE_NAME);
        ArrayList<String> alExercises = new ArrayList<String>();
        if (exercises.moveToFirst()) {
            do {
                alExercises.add(exercises.getString(2));
            } while (exercises.moveToNext());
        }
        ArrayList<Integer> alCount = new ArrayList<Integer>();
        for (String ignored : alExercises) {
            alCount.add(0);
        }
        Cursor allCursor;
        allCursor = db.getDataMain(null, null, null, null, null,
                DB.EXE_NAME);
        if (allCursor.moveToFirst()) {
            do {
                for (int i = 0; i < alExercises.size(); i++) {
                    if (allCursor.getString(2).equals(alExercises.get(i))) {
                        alCount.set(i, (alCount.get(i) + 1));
                    }
                }
            } while (allCursor.moveToNext());
        }
        int max = 0;
        int maxIndex = 0;
        for (int i = 0; i < alCount.size(); i++) {
            if (alCount.get(i) > max) {
                max = alCount.get(i);
                maxIndex = i;
            }
        }
        if (max > 0) {
            result = alExercises.get(maxIndex);
        }
        close();
        return result;
    }

    public static String getWeightDelta(Context context){
        init(context);











        close();
        return null;
    }


}
