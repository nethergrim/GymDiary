package com.nethergrim.combogymdiary.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.TrainingService;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.nethergrim.combogymdiary.dialogs.DialogAddCommentToTraining;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining;
import com.yandex.metrica.Counter;

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
import android.util.Log;
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

	public final static String LOG_TAG = "myLogs";
	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "tra_id";
	public final static String CHECKED_POSITION = "checked_pos";
	public final static String TRAININGS_DONE_NUM = "trainings_done_num";
	protected final static String MINUTES = "minutes";
	protected final static String SECONDS = "seconds";
	public final static String AUTO_BACKUP_TO_DRIVE = "settingAutoBackup";
	public final static String USER_CLICKED_POSITION = "user_clicked_position";
	public final static String TRAINING_LIST = "training_list";
	protected final static String LIST_OF_SETS = "list_of_sets";
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
	private TextView infoText, tvTimerCountdown;
	private ArrayList<String> alMain = new ArrayList<String>();
	private ArrayList<Integer> alSet = new ArrayList<Integer>();
	private int seconds, minutes, secDelta = 0, minDelta = 0, sec, min;
	private Handler timerHandler = new Handler();
	private LinearLayout llBack, llSave, llForward, llBottom, llTimerProgress;
	private ImageView ivBack, ivForward;
	private Animation anim = null;
	private ListView list;
	private int trainingId = 0;
	private TextView tvWeight;
	private boolean isTrainingAtProgress = false;
	private int total = 0;
	private String measureItem = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		trainingId = getArguments().getInt(BasicMenuActivityNew.TRAINING_ID);
		isTrainingAtProgress = sp.getBoolean(TRAINING_AT_PROGRESS, false);
		sp.edit().putInt(TRA_ID, trainingId).apply();
		sp.edit().putBoolean(TRAINING_AT_PROGRESS, true).apply();
		traName = db.getTrainingNameById(trainingId);
		if (db.getTrainingListById(trainingId) != null) {
			exersices = db.convertStringToArray(db
					.getTrainingListById(trainingId));
		} else {
			Counter.sharedInstance()
					.reportEvent(
							"ERROR in db.getTrainingListById(trainingId) at TrainingFragment!!");
		}
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
		tvWeight = (TextView) v.findViewById(R.id.textView4__);
		ivBack = (ImageView) v.findViewById(R.id.imageView2);
		ivForward = (ImageView) v.findViewById(R.id.imageView3);
		reps = (WheelView) v.findViewById(R.id.wheelReps);
		reps.setVisibleItems(7);
		reps.setWheelBackground(R.drawable.wheel_bg_holo);
		reps.setWheelForeground(R.drawable.wheel_val_holo);
		reps.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
		reps.setViewAdapter(new RepsAdapter(getActivity()));
		weights = (WheelView) v.findViewById(R.id.wheelWeight);
		weights.setVisibleItems(7);
		weights.setWheelBackground(R.drawable.wheel_bg_holo);
		weights.setWheelForeground(R.drawable.wheel_val_holo);
		weights.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
		weights.setViewAdapter(new WeightsAdapter(getActivity()));
		tglTimerOn = (ToggleButton) v.findViewById(R.id.tglTurnOff);
		tglTimerOn.setOnCheckedChangeListener(this);
		etTimer = (EditText) v.findViewById(R.id.etTimerValueAtTraining);
		etTimer.setOnClickListener(this);
		infoText = (TextView) v.findViewById(R.id.infoText);
		list = (ListView) v.findViewById(R.id.lvSets);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.my_training_list_item, R.id.tvText, alMain);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				checkedPosition = position;
				sp.edit().putInt(CHECKED_POSITION, position).apply();
				initData(position);
			}
		});
		registerForContextMenu(list);
		list.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
		initData(sp.getInt(CHECKED_POSITION, 0));
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		date = sdf.format(new Date(System.currentTimeMillis()));
		dlg1 = new DialogExitFromTraining();
		dlg1.setCancelable(false);
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
			infoText.setText(getResources().getString(R.string.new_set) + " ("
					+ (set + 1) + ")");
		}
	}

	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "TrainingFragment onResume");
		turnOff = sp.getBoolean("toTurnOff", false);
		list.setKeepScreenOn(!turnOff);
		vibrate = sp.getBoolean("vibrateOn", true);
		String vl = sp.getString("vibtateLenght", "2");
		try {
			vibrateLenght = Integer.parseInt(vl);
		} catch (Exception e) {
			vibrateLenght = 2;
		}

		if (sp.getString(BasicMenuActivityNew.MEASURE_ITEM, "1").equals("1")) {
			tvWeight.setText(getResources().getString(R.string.Weight) + " ("
					+ getResources().getStringArray(R.array.measure_items)[0]
					+ ")");
			measureItem = getResources().getStringArray(R.array.measure_items)[0];
		} else if (sp.getString(BasicMenuActivityNew.MEASURE_ITEM, "1").equals(
				"2")) {
			tvWeight.setText(getResources().getString(R.string.Weight) + " ("
					+ getResources().getStringArray(R.array.measure_items)[1]
					+ ")");
			measureItem = getResources().getStringArray(R.array.measure_items)[1];
		}

		vibrateLenght *= 1000;
		timerHandler.postDelayed(timerRunnable, 0);
		if (isTrainingAtProgress) {
			restoreTimerFromPreferences();
			restoreSetsFromPreferences();
			try {
				list.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
				initData(sp.getInt(CHECKED_POSITION, 0));
			} catch (Exception e) {
				list.setItemChecked(0, true);
				initData(0);
			}
		}
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
						try {
							Vibrator v = (Vibrator) getActivity()
									.getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(vibrateLenght);
						} catch (Exception e) {
							Counter.sharedInstance().reportError(
									"error vibrating", e);
						}
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
		Log.d(LOG_TAG, "TrainingFragment onPause");
		timerHandler.removeCallbacks(timerRunnable);
		saveSetsToPreferences();
		saveTimerToPregerences();
		isTrainingAtProgress = true;
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
		} else if (itemId == R.id.itemAddCommentToTraining) {

			DialogAddCommentToTraining dialog = new DialogAddCommentToTraining();
			dialog.show(getFragmentManager(), "");
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
			total = wei * rep_s;
			total += sp.getInt(BasicMenuActivityNew.TOTAL_WEIGHT, 0);
			sp.edit().putInt(BasicMenuActivityNew.TOTAL_WEIGHT, total).apply();
			db.addRecMainTable(traName, exeName, date, wei, rep_s, set);
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
				infoText.setText(getResources().getString(R.string.new_set)
						+ " (" + (set + 1) + ")");
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
						+ (weitghsS + 1)
						+ "x"
						+ (repsS + 1)
						+ " ("
						+ (currentSet + 1) + ")");
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
						+ (weitghsS + 1)
						+ "x"
						+ (repsS + 1)
						+ " ("
						+ (currentSet + 1) + ")");
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

	Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			long millis = System.currentTimeMillis() - startTime;
			seconds = (int) (millis / 1000);
			minutes += minDelta;
			seconds += secDelta;
			minutes = (seconds / 60);
			seconds = (seconds % 60);
			getActivity().getActionBar().setTitle(
					traName + " "
							+ (String.format("%d:%02d", minutes, seconds))
							+ "  " + total + " " + measureItem);

			timerHandler.postDelayed(this, 500);
		}
	};

	public void saveTimerToPregerences() {
		sp.edit().putInt(SECONDS, seconds).apply();
		sp.edit().putInt(MINUTES, minutes).apply();
	}

	public void restoreTimerFromPreferences() {
		minDelta = sp.getInt(MINUTES, 0);
		secDelta = sp.getInt(SECONDS, 0);
		sp.edit().putInt(MINUTES, 0).apply();
		sp.edit().putInt(SECONDS, 0).apply();
	}

	public void saveSetsToPreferences() {

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < alSet.size(); i++) {
			str.append(alSet.get(i)).append(",");
		}
		sp.edit().putString(LIST_OF_SETS, str.toString()).apply();
	}

	public void restoreSetsFromPreferences() {
		if (sp.contains(LIST_OF_SETS)) {
			String savedString = sp.getString(LIST_OF_SETS, "");
			StringTokenizer st = new StringTokenizer(savedString, ",");
			ArrayList<Integer> array = new ArrayList<Integer>();
			int size = st.countTokens();
			for (int i = 0; i < size; i++) {
				try {
					array.add(Integer.parseInt(st.nextToken()));
				} catch (Exception e) {
					array.add(0);
				}
			}
			alSet = array;
		}
	}

}
