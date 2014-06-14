package com.nethergrim.combogymdiary.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class AddingProgramActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>, OnClickListener {

    private Button btnAdd;
    private EditText etName;
    private ListView lvExe;
    private DB db;
    private SimpleCursorAdapter adapter;

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initUi();
    }

    private void initUi() {
        setContentView(R.layout.adding_program);
        btnAdd = (Button) findViewById(R.id.buttonAddingProgram);
        btnAdd.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.etTimerValue);
        getActionBar().setTitle(R.string.creating_program);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        lvExe = (ListView) findViewById(R.id.listView1);
        lvExe.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        db = new DB(this);
        db.open();
        String[] from = new String[]{DB.EXE_NAME};
        int[] to = new int[]{android.R.id.text1,};
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, null, from,
                to, 0);
        lvExe.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();

    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().getLoader(0).forceLoad();
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.buttonAddingProgram) {
            String prgName = etName.getText().toString();
            long[] arrIDs = lvExe.getCheckedItemIds();

            if (!prgName.isEmpty() && arrIDs.length > 0) {

                Cursor c = db.getDataExe(null, null, null, null, null, null);
                c.moveToFirst();
                String[] exersices = new String[arrIDs.length];
                int j = 0;
                do {
                    if (c.getInt(0) == arrIDs[j]) {
                        exersices[j] = c.getString(2);
                        j++;
                    }
                } while (c.moveToNext() && j < arrIDs.length);

                db.addRecTrainings(prgName, db.convertArrayToString(exersices));
                finish();
            } else {
                Toast.makeText(this, R.string.input_data, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;
        Cursor cursor;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            cursor = db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
            return cursor;
        }
    }

}
