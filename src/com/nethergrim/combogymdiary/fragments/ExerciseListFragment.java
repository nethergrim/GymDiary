package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercise;

public class ExerciseListFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ListView listview;
	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	private static final int CM_DELETE_ID = 1;
	private static final int CM_EDIT_ID = 2;
	private DB db;
	private SimpleCursorAdapter scAdapter;
	private SharedPreferences sp;
	private int LOADER_ID = 1;
	private OnExerciseEdit mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnExerciseEdit) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
		String[] from = new String[] { DB.EXE_NAME };
		int[] to = new int[] { R.id.tvText, };
		scAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.my_list_item2, null, from, to, 0);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.exersises_list, null);
		getActivity().getActionBar().setTitle(
				R.string.excersisiesListButtonString);
		listview = (ListView) v.findViewById(R.id.listView11);
		listview.setAdapter(scAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goToEditExe(position, id);
			}
		});
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(LOADER_ID, null, this);
		return v;
	}

	private void goToEditExe(int position, long ID) {
		mListener.onExerciseEdit(position, ID);
	}

	public static interface OnExerciseEdit {
		public void onExerciseEdit(int pos, long id);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(listview);
	}

	public void onResume() {
		super.onResume();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(LOADER_ID).forceLoad();
	}

	public void onPause() {
		super.onPause();
		unregisterForContextMenu(listview);
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
			DialogAddExercise dialog = new DialogAddExercise();
			dialog.show(getFragmentManager(), "tag");
			return true;
		}
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		if (id == LOADER_ID)
			return new MyCursorLoader(getActivity(), db);
		else
			return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.isClosed()) {
			scAdapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		scAdapter.swapCursor(null);
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
			return db.getDataExe(null, null, null, null, null, DB.EXE_NAME);
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
				db.deleteExersice(exeName);
				Toast.makeText(getActivity(), R.string.deleted,
						Toast.LENGTH_SHORT).show();
				((FragmentActivity) getActivity()).getSupportLoaderManager()
						.getLoader(LOADER_ID).forceLoad();
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
					.getLoader(LOADER_ID).forceLoad();
			return true;
		}
		return super.onContextItemSelected(item);
	}
}
