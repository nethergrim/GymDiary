package com.nethergrim.combogymdiary.fragments;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.AddingExersisesActivity;
import com.nethergrim.combogymdiary.activities.BasicMenuActivity;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ExerciseListFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ListView lvExersices_list;
	private String TRAINING_AT_PROGRESS = BasicMenuActivity.TRAINING_AT_PROGRESS;
	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID = 2;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private SharedPreferences sp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.exersises_list, null);
		getActivity().getActionBar().setTitle(
				R.string.excersisiesListButtonString);
		lvExersices_list = (ListView) v.findViewById(R.id.listView11);
		db = new DB(getActivity());
		db.open();
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.my_list_item2, null, from, to, 0);

		lvExersices_list.setAdapter(scAdapter);
		lvExersices_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goToEditExe(position, id);
			}
		});
		return v;
	}

	private void goToEditExe(int position, long ID) {
		if (sp.getBoolean(BasicMenuActivity.TRAINING_AT_PROGRESS, false)) {
			Toast.makeText(getActivity(), R.string.error_editing_exe,
					Toast.LENGTH_SHORT).show();
		} else {
			String[] cols = { DB.COLUMN_ID, DB.EXE_NAME, DB.TIMER_VALUE };
			Cursor cursor_exe = db.getDataExe(cols, null, null, null, null,
					DB.EXE_NAME);
			cursor_exe.moveToFirst();
			while (cursor_exe.getPosition() < position) {
				cursor_exe.moveToNext();
			}
			String name = cursor_exe.getString(1);
			Toast.makeText(getActivity(), "Editing: " + name,
					Toast.LENGTH_SHORT).show();
			String timV = cursor_exe.getString(2);
			cursor_exe.close();
			Intent intent_exe_edit = new Intent(getActivity(),
					AddingExersisesActivity.class);
			intent_exe_edit.putExtra("exeName", name);
			intent_exe_edit.putExtra("timerValue", timV);
			intent_exe_edit.putExtra("exePosition", position);
			intent_exe_edit.putExtra("exeID", ID);
			startActivity(intent_exe_edit);
		}
	}

	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	  super.onActivityCreated(savedInstanceState);
	  registerForContextMenu(lvExersices_list);
	}

	public void onResume() {
		super.onResume();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(1, null, this);
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(1).forceLoad();
		getActivity().invalidateOptionsMenu();
		
	}

	public void onPause() {
		super.onPause();
		unregisterForContextMenu(lvExersices_list);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.clear();
		inflater.inflate(R.menu.exercise_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.itemAddNewExe) {
			Intent gotoAddingExersisesActivity = new Intent(getActivity(),
					AddingExersisesActivity.class);
			startActivity(gotoAddingExersisesActivity);

			return true;
		}
		return false;
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

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		menu.add(0, CM_EDIT_ID, 0, R.string.edit);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {

			TextView tvTmp = (TextView) acmi.targetView;
			String exeName = tvTmp.getText().toString();

			if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
				Toast.makeText(getActivity(), R.string.error_deleting_exe,
						Toast.LENGTH_SHORT).show();
			} else {
				db.delRec_Exe(acmi.id);
				db.deleteExersiceByName(exeName);
				Toast.makeText(getActivity(), R.string.deleted,
						Toast.LENGTH_SHORT).show();
				((FragmentActivity) getActivity()).getSupportLoaderManager()
						.getLoader(1).forceLoad();
			}

			return true;
		} else if (item.getItemId() == CM_EDIT_ID) {
			if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
				Toast.makeText(getActivity(), R.string.error_editing_exe,
						Toast.LENGTH_SHORT).show();
			} else {
				goToEditExe(acmi.position, acmi.id);
			}
			((FragmentActivity) getActivity()).getSupportLoaderManager()
					.getLoader(1).forceLoad();
			return true;
		}
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(1).forceLoad();
		return super.onContextItemSelected(item);
	}
}
