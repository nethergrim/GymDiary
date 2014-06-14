package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.AddingProgramActivity;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.dialogs.DialogGoToMarket;

public class StartTrainingFragment extends Fragment implements
        LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 3;
    private static final int CM_EDIT_ID = 4;
    private ListView lvMain;
    private DB db;
    private Cursor cursor;
    private SimpleCursorAdapter scAdapter;
    private OnSelectedListener mCallback;
    private int LOADER_ID = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        db = new DB(getActivity());
        db.open();
        String[] from = new String[]{DB.TRA_NAME};
        int[] to = new int[]{R.id.tvText,};
        scAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.my_list_item, null, from, to, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(lvMain);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_training, null);
        lvMain = (ListView) v.findViewById(R.id.lvStartTraining);
        getActivity().getActionBar().setTitle(
                R.string.startTrainingButtonString);

        FrameLayout fl = (FrameLayout) v.findViewById(R.id.frameAd);
        fl.setVisibility(View.GONE);
        lvMain.setAdapter(scAdapter);
        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goToTraining((int) id);
            }
        });
        ((FragmentActivity) getActivity()).getSupportLoaderManager()
                .initLoader(LOADER_ID, null, this);
        return v;
    }

    public void onPause() {
        super.onPause();
        unregisterForContextMenu(lvMain);
    }

    public void onStart() {
        super.onStart();

    }

    public void onResume() {
        super.onResume();
        ((FragmentActivity) getActivity()).getSupportLoaderManager()
                .getLoader(LOADER_ID).forceLoad();
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (sp.contains(BasicMenuActivityNew.TRAININGS_DONE_NUM)
                && sp.getInt(BasicMenuActivityNew.TRAININGS_DONE_NUM, 0) > 5
                && !sp.contains(BasicMenuActivityNew.MARKET_LEAVED_FEEDBACK)) {
            DialogFragment dialog = new DialogGoToMarket();
            dialog.show(getActivity().getFragmentManager(),
                    "dialog_goto_market");
            dialog.setCancelable(false);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.start_training_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemAddNewProgramm) {
            Intent gotoAddingProgramActivity = new Intent(getActivity(),
                    AddingProgramActivity.class);
            startActivity(gotoAddingProgramActivity);

            return true;
        }
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(1, CM_EDIT_ID, 0, R.string.edit);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            cursor = db.getDataTrainings(null, null, null, null, null, null);
            LinearLayout llTmp = (LinearLayout) acmi.targetView;
            TextView tvTmp = (TextView) llTmp.findViewById(R.id.tvText);
            String traName = tvTmp.getText().toString();
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(1).equals(traName)) {
                        db.delRec_Trainings(cursor.getInt(0));
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.deleted),
                                Toast.LENGTH_SHORT).show();
                    }
                } while (cursor.moveToNext());
                ((FragmentActivity) getActivity()).getSupportLoaderManager()
                        .getLoader(LOADER_ID).forceLoad();
                cursor.close();
                return true;
            }
        } else if (item.getItemId() == CM_EDIT_ID) {
            long id = acmi.id;
            Intent intent = new Intent(getActivity(),
                    EditingProgramAtTrainingActivity.class);
            intent.putExtra("trID", id);
            intent.putExtra("ifAddingExe", false);
            startActivityForResult(intent, 1);
            ((FragmentActivity) getActivity()).getSupportLoaderManager()
                    .getLoader(LOADER_ID).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void goToTraining(int id) {
        mCallback.onTrainingSelected(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(getActivity(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scAdapter.swapCursor(null);
    }

    public interface OnSelectedListener {
        public void onTrainingSelected(int id);
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
            cursor = db.getDataTrainings(null, null, null, null, null, null);
            return cursor;
        }
    }
}
