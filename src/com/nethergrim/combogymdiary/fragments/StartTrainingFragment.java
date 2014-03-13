package com.nethergrim.combogymdiary.fragments;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.AddingProgramActivity;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.TrainingAtProgress;
import com.nethergrim.combogymdiary.dialogs.DialogGoToMarket;

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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class StartTrainingFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	final String LOG_TAG = "myLogs";
	private ListView lvMain;
	private DB db;
	private Cursor cursor_exe;
	private SimpleCursorAdapter scAdapter;
	private static final int CM_DELETE_ID = 3;
	private static final int CM_EDIT_ID = 4;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "StartTrainingFragment onCreate");
		setRetainInstance(true);
		setHasOptionsMenu(true);
		db = new DB(getActivity());
		db.open();
		cursor_exe = db.getDataTrainings(null, null, null, null, null, null);

		String[] from = new String[] { DB.TRA_NAME };
		int[] to = new int[] { R.id.tvText, };
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
		Log.d(LOG_TAG, "StartTrainingFragment onCreateView");
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
		return v;
	}

	public void onPause() {
		super.onPause();
		unregisterForContextMenu(lvMain);
	}

	public void onStart() {
		super.onStart();
		Log.d(LOG_TAG, "StartTrainingFragment onStart");
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.initLoader(0, null, this);
	}

	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "StartTrainingFragment onResume");
		((FragmentActivity) getActivity()).getSupportLoaderManager()
				.getLoader(0).forceLoad();
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
			LinearLayout llTmp = (LinearLayout) acmi.targetView;
			TextView tvTmp = (TextView) llTmp.findViewById(R.id.tvText);
			String traName = tvTmp.getText().toString();
			if (cursor_exe.moveToFirst()) {
				do {
					if (cursor_exe.getString(1).equals(traName)) {
						db.delRec_Trainings(cursor_exe.getInt(0));
						Toast.makeText(getActivity(),
								getResources().getString(R.string.deleted),
								Toast.LENGTH_SHORT).show();
						((FragmentActivity) getActivity())
								.getSupportLoaderManager().getLoader(0)
								.forceLoad();
					}
				} while (cursor_exe.moveToNext());
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
					.getLoader(0).forceLoad();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void goToTraining(int id) {
		if (cursor_exe.moveToFirst()) {
			String str = null;
			do {
				if (cursor_exe.getInt(0) == id) {
					str = cursor_exe.getString(1);
				}
			} while (cursor_exe.moveToNext());
			Intent intent_to_trainng = new Intent(getActivity(),
					TrainingAtProgress.class);
			if (str != null && !str.isEmpty()) {
				intent_to_trainng.putExtra("trainingName", str);
				startActivity(intent_to_trainng);
			}
		}
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
			cursor = db.getDataTrainings(null, null, null, null, null, null);
			return cursor;
		}
	}
}
