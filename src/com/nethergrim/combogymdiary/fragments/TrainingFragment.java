package com.nethergrim.combogymdiary.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import com.mobeta.android.dslv.DragSortListView;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.TrainingService;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TrainingFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {

	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "tra_id";
	public final static String TRAININGS_DONE_NUM = "trainings_done_num";
	public final static String AUTO_BACKUP_TO_DRIVE = "settingAutoBackup";
	public final static String USER_CLICKED_POSITION = "user_clicked_position";
	public final static String TRAINING_LIST = "training_list";
	public final static String PROGRESS = "progress";
	public final static String TIMER_IS_ON = "timerIsOn";
	private ToggleButton tglTimerOn;
	private Boolean tglChecked = true, turnOff = false, vibrate = false;
	private EditText etTimer;
	private DB db;
	private ArrayAdapter<String> adapter;
	private static final int CM_DELETE_ID = 6;
	private String[] exersices;
	private String traName = "", exeName = "", date = "", tValue = "";
	private SharedPreferences sp;
	private int checkedPosition = 0, set = 0, currentSet = 0, oldReps = 0,
			oldWeight = 0, timerValue = 0, vibrateLenght = 0, currentId = 0;
	private DialogFragment dlg1;
	private Boolean isProgressBarActive = false;
	private long startTime = 0;
	private Handler h;
	private WheelView reps, weights;
	private ProgressBar pb;
	private TextView infoText, setInfo, tvTimerCountdown;
	private ArrayList<String> alMain = new ArrayList<String>();
	private ArrayList<Integer> alSet = new ArrayList<Integer>();
	private int seconds, minutes, secDelta = 0, minDelta = 0, sec, min;
	private Handler timerHandler = new Handler();
	private LinearLayout llBack, llSave, llForward, llBottom, llTimerProgress;
	private ImageView ivBack, ivForward;
	private Animation anim = null;
	private DragSortListView list;
	private int trainingId = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		trainingId = getArguments().getInt(BasicMenuActivityNew.TRAINING_ID);
		sp.edit().putInt(TRA_ID, trainingId).apply();
		sp.edit().putBoolean(TRAINING_AT_PROGRESS, true).apply();
		traName = db.getTrainingNameById(trainingId);
		exersices = db.convertStringToArray(db.getTrainingListById(trainingId));
		for (int i = 0; i < exersices.length; i++) {
			alMain.add(exersices[i]);
		}
		for (int i = 0; i < 200; i++) {
			alSet.add(0);
		}
		getActivity().startService(
				new Intent(getActivity(), TrainingService.class));
		startTime = System.currentTimeMillis();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.training_at_progress_new_wheel_new_list, null);
		getActivity().getActionBar().setTitle(traName);

		llTimerProgress = (LinearLayout) v.findViewById(R.id.llProgressShow);
		if (isProgressBarActive) {
			llTimerProgress.setVisibility(View.VISIBLE);
		} else
			llTimerProgress.setVisibility(View.GONE);

		tvTimerCountdown = (TextView) v.findViewById(R.id.tvTimerCountdown);
		pb = (ProgressBar) v.findViewById(R.id.pbTrainingRest);
		llBottom = (LinearLayout) v.findViewById(R.id.LLBottom);
		anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.setfortraining);
		llBack = (LinearLayout) v.findViewById(R.id.llBtnBack);
		llSave = (LinearLayout) v.findViewById(R.id.llBtnSave);
		llForward = (LinearLayout) v.findViewById(R.id.llBtnForward);
		llBack.setOnClickListener(this);
		llSave.setOnClickListener(this);
		llForward.setOnClickListener(this);
		llBack.setEnabled(false);
		llForward.setEnabled(false);
		ivBack = (ImageView) v.findViewById(R.id.imageView2);
		ivForward = (ImageView) v.findViewById(R.id.imageView3);
		reps = (WheelView) v.findViewById(R.id.wheelReps);
		reps.setVisibleItems(5);
		reps.setWheelBackground(R.drawable.wheel_bg_holo);
		reps.setWheelForeground(R.drawable.wheel_val_holo);
		reps.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
		reps.setViewAdapter(new RepsAdapter(getActivity()));
		weights = (WheelView) v.findViewById(R.id.wheelWeight);
		weights.setVisibleItems(5);
		weights.setWheelBackground(R.drawable.wheel_bg_holo);
		weights.setWheelForeground(R.drawable.wheel_val_holo);
		weights.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
		weights.setViewAdapter(new WeightsAdapter(getActivity()));
		tglTimerOn = (ToggleButton) v.findViewById(R.id.tglTurnOff);
		tglTimerOn.setOnCheckedChangeListener(this);
		etTimer = (EditText) v.findViewById(R.id.etTimerValueAtTraining);
		etTimer.setOnClickListener(this);
		infoText = (TextView) v.findViewById(R.id.infoText);
		setInfo = (TextView) v.findViewById(R.id.tvSetInfo);
		list = (DragSortListView) v.findViewById(R.id.lvSets);
		list.setDropListener(onDrop);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_radio, R.id.text, alMain);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				checkedPosition = position;
				sp.edit().putInt(USER_CLICKED_POSITION, position).apply();
				initData(position);
			}
		});
		registerForContextMenu(list);
		list.setItemChecked(0, true);
		initData(0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		date = sdf.format(new Date(System.currentTimeMillis()));
		dlg1 = new DialogExitFromTraining();
		dlg1.setCancelable(false);
		setInfo.setTextColor(getResources().getColor(R.color.holo_orange_dark));
		infoText.setTextColor(getResources().getColor(R.color.holo_orange_dark));
		boolean isTimerOn = sp.getBoolean(TIMER_IS_ON, false);
		if (isTimerOn) {
			tglTimerOn.setChecked(true);
			tglChecked = true;
			etTimer.setEnabled(true);
		} else {
			tglTimerOn.setChecked(false);
			tglChecked = false;
			etTimer.setEnabled(false);
		}

		return v;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(5, CM_DELETE_ID, 0, R.string.delete_record);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			String nameToDelete = alMain.get(acmi.position);
			alMain.remove(acmi.position);
			alSet.remove(acmi.position);
			db.deleteExersiceByName(nameToDelete, traName);
			adapter.notifyDataSetChanged();
			if (alMain.size() > 0) {
				initData(0);
				list.setItemChecked(0, true);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void initSetButtons() {
		if (set > 0 && currentSet > 0) {
			llBack.setEnabled(true);
			ivBack.setAlpha(1.0F);
		} else {
			llBack.setEnabled(false);
			ivBack.setAlpha(0.35F);
		}
		if (currentSet < set) {
			llForward.setEnabled(true);
			ivForward.setAlpha(1.0F);
		} else {
			llForward.setEnabled(false);
			ivForward.setAlpha(0.35F);
		}
	}

	private void initData(int position) {
		exeName = alMain.get(position);
		try {
			pb.setMax(Integer.parseInt(db.getTimerValueByExerciseName(exeName)));
		} catch (Exception e) {
			pb.setMax(30);
		}

		set = alSet.get(position);
		currentSet = set;
		setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  "
				+ getResources().getString(R.string.set_number) + " "
				+ (set + 1));
		tValue = db.getTimerValueByExerciseName(exeName);
		etTimer.setText(tValue);
		initSetButtons();
		oldReps = db.getLastReps(exeName, set);
		oldWeight = db.getLastWeight(exeName, set);
		if (oldReps > 0 && oldWeight > 0) {
			infoText.setText(getResources().getString(
					R.string.previous_result_was)
					+ " " + oldWeight + "x" + oldReps);
			weights.setCurrentItem(oldWeight - 1);
			reps.setCurrentItem(oldReps - 1);
		} else {
			infoText.setText(getResources().getString(R.string.new_set));
		}
	}

	public void onResume() {
		super.onResume();
		turnOff = sp.getBoolean("toTurnOff", false);
		list.setKeepScreenOn(!turnOff);
		vibrate = sp.getBoolean("vibrateOn", true);
		String vl = sp.getString("vibtateLenght", "2");
		try {
			vibrateLenght = Integer.parseInt(vl);
		} catch (Exception e) {
			vibrateLenght = 2;
		}
		vibrateLenght *= 1000;
		timerHandler.postDelayed(timerRunnable, 0);
	}

	@SuppressLint("HandlerLeak")
	private void goDialogProgress() {

		try {
			timerValue = Integer.parseInt(db
					.getTimerValueByExerciseName(exeName));
		} catch (Exception e) {
			timerValue = 60;
		}
		pb.setMax(timerValue);
		sec = timerValue % 60;
		min = timerValue / 60;
		h = new Handler() {
			public void handleMessage(Message msg) {
				if (sec > 0) {
					sec--;
				} else if (sec == 0 && min > 0) {
					sec = 59;
					min--;
				}
				if (pb.getProgress() < pb.getMax()) {
					h.sendEmptyMessageDelayed(0, 1000);
					pb.setProgress(sp.getInt(PROGRESS, 0));
					pb.incrementProgressBy(1);
					tvTimerCountdown.setText("" + min + ":" + sec);
					sp.edit().putInt(PROGRESS, pb.getProgress()).apply();
				} else {
					if (vibrate) {
						Vibrator v = (Vibrator) getActivity().getSystemService(
								Context.VIBRATOR_SERVICE);
						v.vibrate(vibrateLenght);
					}
					llTimerProgress.setVisibility(View.GONE);
					pb.setProgress(0);
					tvTimerCountdown.setText("");
					isProgressBarActive = false;
				}
			}
		};
		if (!isProgressBarActive) {
			h.sendEmptyMessageDelayed(0, 10);
			llTimerProgress.setVisibility(View.VISIBLE);
		}
		isProgressBarActive = true;
	}

	public void onPause() {
		super.onPause();
		timerHandler.removeCallbacks(timerRunnable);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.itemExit) {
			dlg1.show(getFragmentManager(), "dlg1");
		} else if (itemId == R.id.itemEditTrainings) {
			Intent intent = new Intent(getActivity(),
					EditingProgramAtTrainingActivity.class);
			intent.putExtra("trName", traName);
			intent.putExtra("ifAddingExe", true);
			startActivityForResult(intent, 1);
		} else if (itemId == R.id.itemSeePreviousTraining) {
			String[] args = { traName };
			Cursor tmpCursor = db.getDataMain(null, DB.TRA_NAME + "=?", args,
					DB.DATE, null, null);
			if (tmpCursor.moveToLast()
					&& (tmpCursor.getCount() > 1 || !tmpCursor.getString(3)
							.equals(date))) {

				if (tmpCursor.getString(3).equals(date)) { // сегодня уже были
															// сеты
					tmpCursor.moveToPrevious();
				}

				Intent intent_history_detailed = new Intent(getActivity(),
						HistoryDetailedActivity.class);
				intent_history_detailed
						.putExtra("date", tmpCursor.getString(3));
				intent_history_detailed.putExtra("trName",
						tmpCursor.getString(1));
				startActivity(intent_history_detailed);

			} else {
				Toast.makeText(
						getActivity(),
						getResources().getString(R.string.no_history) + traName,
						Toast.LENGTH_SHORT).show();
			}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}

		long[] itemsChecked = data
				.getLongArrayExtra("return_array_of_exersices");
		for (int i = 0; i < itemsChecked.length; i++) {

			alMain.add(db.getExerciseByID((int) itemsChecked[i]));
			alSet.add(0);
		}
		for (int j = 0; j < 100; j++) {
			alSet.add(0);
		}
		adapter.notifyDataSetChanged();
		String[] tmp = new String[alMain.size()];

		for (int i = 0; i < alMain.size(); i++) {
			tmp[i] = alMain.get(i);
		}
		db.updateRec_Training(trainingId, 2, db.convertArrayToString(tmp));

	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		String tmpStr = db.getTimerValueByExerciseName(exeName);
		String timerv = etTimer.getText().toString();
		if (!tmpStr.equals(timerv)) { // re-write to DB timer value for an
										// exercise
			int exe_id = db.getExeIdByName(exeName);
			db.updateRec_Exe(exe_id, DB.TIMER_VALUE, timerv);
		}

		if (id == R.id.llBtnSave && currentSet == set) {
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem() + 1);
			int tmp = alSet.get(checkedPosition);
			tmp++;
			alSet.set(checkedPosition, tmp);
			set = alSet.get(checkedPosition);
			setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  "
					+ getResources().getString(R.string.set_number) + " "
					+ (set + 1));
			db.addRec_Main(traName, exeName, date, wei, rep_s, set);
			currentSet = set;
			initSetButtons();
			Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT)
					.show();
			oldReps = db.getLastReps(exeName, set);
			oldWeight = db.getLastWeight(exeName, set);
			if (oldReps > 0 && oldWeight > 0) {
				infoText.setText(getResources().getString(
						R.string.previous_result_was)
						+ " " + oldWeight + "x" + oldReps);
				weights.setCurrentItem(oldWeight - 1);
				reps.setCurrentItem(oldReps - 1);
			} else {
				infoText.setText(getResources().getString(R.string.new_set));
			}
			if (tglChecked && !isProgressBarActive) {
				sp.edit().putInt(PROGRESS, 0).apply();
				goDialogProgress();
			}
		} else if (id == R.id.llBtnSave && currentSet < set) {
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem() + 1);
			db.updateRec_Main(currentId, 4, null, wei);
			db.updateRec_Main(currentId, 5, null, rep_s);
			Toast.makeText(getActivity(), R.string.resaved, Toast.LENGTH_SHORT)
					.show();
			currentSet = set;
			initData(checkedPosition);
		} else if (id == R.id.llBtnBack) {
			if (currentSet > 0) {
				llBottom.startAnimation(anim);
				currentSet--;
				int weitghsS = db.getThisWeight(currentSet + 1, exeName) - 1;
				int repsS = db.getThisReps(currentSet + 1, exeName) - 1;
				currentId = db.getThisId(currentSet + 1, exeName);
				weights.setCurrentItem(weitghsS);
				reps.setCurrentItem(repsS);
				infoText.setText(getResources()
						.getString(R.string.resaved_text)
						+ " "
						+ (weitghsS + 1) + "x" + (repsS + 1));
			}

		} else if (id == R.id.llBtnForward) {
			if (currentSet < set - 1) {
				llBottom.startAnimation(anim);
				currentSet++;
				int weitghsS = db.getThisWeight(currentSet + 1, exeName) - 1;
				int repsS = db.getThisReps(currentSet + 1, exeName) - 1;
				weights.setCurrentItem(weitghsS);
				reps.setCurrentItem(repsS);
				infoText.setText(getResources()
						.getString(R.string.resaved_text)
						+ " "
						+ (weitghsS + 1) + "x" + (repsS + 1));
			} else if (currentSet == set - 1) {
				llBottom.startAnimation(anim);
				initData(checkedPosition);
			}
		}
		initSetButtons();
	}

	@Override
	public void onCheckedChanged(CompoundButton tglTimerOn, boolean isChecked) {
		if (isChecked) {
			sp.edit().putBoolean(TIMER_IS_ON, true).apply();
			tglChecked = true;
			etTimer.setEnabled(true);
		} else {
			sp.edit().putBoolean(TIMER_IS_ON, false).apply();
			tglChecked = false;
			etTimer.setEnabled(false);
		}
	}

	private class RepsAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> reps = new ArrayList<String>();

		protected RepsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);

			for (int i = 0; i < 300; i++) {
				reps.add("" + (i + 1));
			}

			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return reps.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return reps.get(index);
		}
	}

	private class WeightsAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> weights = new ArrayList<String>();

		protected WeightsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);

			for (int i = 0; i < 1000; i++) {
				weights.add("" + (i + 1));
			}

			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return weights.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return weights.get(index);
		}
	}

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				DragSortListView list = (DragSortListView) getActivity()
						.findViewById(R.id.lvSets);
				String item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
				list.moveCheckState(from, to);
				String[] tmp = new String[alMain.size()];
				for (int i = 0; i < alMain.size(); i++) {
					tmp[i] = alMain.get(i);
				}
				db.updateRec_Training(trainingId, 2,
						db.convertArrayToString(tmp));
			}
		}
	};

	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			long millis = System.currentTimeMillis() - startTime;
			seconds = (int) (millis / 1000);
			minutes += minDelta;
			seconds += secDelta;
			minutes = (seconds / 60);
			seconds = (seconds % 60);

			setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  "
					+ getResources().getString(R.string.set_number) + " "
					+ (currentSet + 1));

			timerHandler.postDelayed(this, 500);
		}
	};

}
