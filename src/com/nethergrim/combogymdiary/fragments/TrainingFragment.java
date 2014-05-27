package com.nethergrim.combogymdiary.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.DynamicListView;
import com.nethergrim.combogymdiary.DynamicListView.onElementsSwapped;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.StableArrayAdapter;
import com.nethergrim.combogymdiary.TrainingService;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.nethergrim.combogymdiary.dialogs.DialogAddCommentToTraining;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining;
import com.yandex.metrica.Counter;

public class TrainingFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener, onElementsSwapped {

	public final static String LOG_TAG = "myLogs";
	public String RINGTONE = "ringtone";
	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "tra_id";
	public final static String CHECKED_POSITION = "checked_pos";
	public final static String TRAININGS_DONE_NUM = "trainings_done_num";
	private final static String START_TIME = "start_time";
	private final static String LIST_OF_SETS = "list_of_sets";
	private final static String PROGRESS = "progress";
	private static final String TOTAL_WEIGHT = "total_weight";
	private final static String TIMER_IS_ON = "timerIsOn";
	private ToggleButton tglTimerOn;
	private ActionBar bar;
	private Boolean tglChecked = true, turnOff = false, vibrate = false;
	private EditText etTimer;
	private DB db;
	private static final int CM_DELETE_ID = 6;
	private String[] exersices;
	private String traName = "", exeName = "", date = "";
	private SharedPreferences sp;
	private int checkedPosition = 0, set = 0, currentSet = 0, oldReps = 0,
			oldWeight = 0, timerValue = 0, vibrateLenght = 0, currentId = 0;
	private DialogFragment dlg1;
	private long startTime = 0;
	private Handler h;
	private MediaPlayer mMediaPlayer;
	private WheelView reps, weights;
	private TextView infoText;
	private ArrayList<String> alExersicesList = new ArrayList<String>();
	private ArrayList<Integer> alSetList = new ArrayList<Integer>();
	private int seconds, minutes;
	private Handler timerHandler = new Handler();
	private LinearLayout llBack, llSave, llForward, llBottom, llTimerProgress;
	private ImageView ivBack, ivForward;
	private Animation anim = null;
	private int trainingId = 0;
	private TextView tvWeight;
	private boolean isTrainingAtProgress = false, toPlaySound = false;
	private int total = 0;
	private ProgressDialog pd;
	private String measureItem = "";
	private boolean isActiveDialog = false, blocked = false;
	private DynamicListView listView;
	private StableArrayAdapter adapter;

	@SuppressWarnings("rawtypes")
	@Override
	public void onSwapped(ArrayList arrayList, int indexOne, int indexTwo) {
		swapElements(alSetList, indexOne, indexTwo);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void swapElements(ArrayList arrayList, int indexOne, int indexTwo) {
		Object temp = arrayList.get(indexOne);
		arrayList.set(indexOne, arrayList.get(indexTwo));
		arrayList.set(indexTwo, temp);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "training fragment onCreate");
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		trainingId = getArguments().getInt(BasicMenuActivityNew.TRAINING_ID);
		isTrainingAtProgress = sp.getBoolean(TRAINING_AT_PROGRESS, false);
		sp.edit().putInt(TRA_ID, trainingId).apply();
		sp.edit().putBoolean(TRAINING_AT_PROGRESS, true).apply();
		traName = db.getTrainingName(trainingId);
		bar = getActivity().getActionBar();
		bar.setTitle(traName);
		if (db.getTrainingList(trainingId) != null) {
			exersices = db.convertStringToArray(db.getTrainingList(trainingId));
			for (int i = 0; i < exersices.length; i++) {
				alExersicesList.add(exersices[i]);
			}
		} else {
			Counter.sharedInstance()
					.reportEvent(
							"ERROR in db.getTrainingListById(trainingId) at TrainingFragment!!");
			alExersicesList.add("empty");
		}

		for (int i = 0; i < 150; i++) {
			alSetList.add(0);
		}
		getActivity().startService(
				new Intent(getActivity(), TrainingService.class));
		startTime = System.currentTimeMillis();
		sp.edit().putLong(START_TIME, startTime);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG, "training fragment onCreateView");
		View v = inflater.inflate(
				R.layout.training_at_progress_new_wheel_new_list, null);
		llTimerProgress = (LinearLayout) v.findViewById(R.id.llProgressShow);

		llTimerProgress.setVisibility(View.GONE);
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
		etTimer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				updateTimer(s.toString());
			}
		});

		infoText = (TextView) v.findViewById(R.id.infoText);
		listView = (DynamicListView) v.findViewById(R.id.listViewExerciseList);

		listView.setList(alExersicesList);
		listView.setFragment(this);
		adapter = new StableArrayAdapter(getActivity(),
				R.layout.my_training_list_item, alExersicesList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				onSelected(position);
			}
		});
		listView.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
		onSelected(sp.getInt(CHECKED_POSITION, 0));
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
		menu.add(5, CM_DELETE_ID, 0, R.string.delete_from_this_list);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			String nameToDelete = alExersicesList.get(acmi.position);
			alExersicesList.remove(acmi.position);
			alSetList.remove(acmi.position);
			db.deleteExersice(nameToDelete, traName);
			adapter.notifyDataSetChanged();
			if (alExersicesList.size() > 0) {
				onSelected(0);
				for (int i = 0; i < listView.getCount(); i++) {
					listView.setItemChecked(i, false);
				}
				blocked = true;
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

	private void onSelected(int position) {
		sp.edit().putInt(CHECKED_POSITION, position).apply();
		checkedPosition = position;
		exeName = alExersicesList.get(position);
		set = alSetList.get(position);
		try {
			timerValue = Integer.parseInt(db
					.getTimerValueByExerciseName(exeName));
		} catch (NumberFormatException e) {
			Toast.makeText(getActivity(), R.string.parsing_error,
					Toast.LENGTH_LONG).show();
			timerValue = 60;
			Counter.sharedInstance().reportError("", e);
		}
		currentSet = set;
		etTimer.setText(String.valueOf(timerValue));
		initSetButtons();
		oldReps = db.getLastWeightOrReps(exeName, set, false);
		oldWeight = db.getLastWeightOrReps(exeName, set, true);
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
		blocked = false;
	}

	@SuppressLint("HandlerLeak")
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "training fragment onResume");
		turnOff = sp.getBoolean("toTurnOff", false);
		listView.setKeepScreenOn(!turnOff);
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
		toPlaySound = sp.getBoolean("toNotifyWithSound", true);
		if (isTrainingAtProgress) {
			total = sp.getInt(TOTAL_WEIGHT, 0);
			startTime = sp.getLong(START_TIME, 0);
			restoreSetsFromPreferences();
			try {
				listView.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
				onSelected(sp.getInt(CHECKED_POSITION, 0));
			} catch (Exception e) {
				listView.setItemChecked(0, true);
				onSelected(0);
			}
		}
		timerHandler.postDelayed(timerRunnable, 0);

		h = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					this.removeMessages(0);
				} else {
					pd.setIndeterminate(false);
					if (pd.getProgress() < pd.getMax()) {
						pd.incrementProgressBy(1);
						h.sendEmptyMessageDelayed(1, 1000);
					} else {
						if (toPlaySound) {
							String sound = sp.getString(RINGTONE, null);

							if (sound == null) {
								playSound(
										getActivity(),
										RingtoneManager
												.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
							} else {
								Log.d(LOG_TAG, "Playing: " + sound);
								Uri uri = Uri.parse(sound);
								playSound(getActivity(), uri);
							}
						}
						if (vibrate) {
							try {
								Vibrator v = (Vibrator) getActivity()
										.getSystemService(
												Context.VIBRATOR_SERVICE);
								v.vibrate(vibrateLenght);
							} catch (Exception e) {
								Counter.sharedInstance().reportError(
										"error vibrating", e);
							}
						}
						pd.dismiss();
					}
				}

			}
		};

	}

	private void goDialogProgress() {
		pd = new ProgressDialog(getActivity());
		pd.setTitle(R.string.resting);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(timerValue);
		pd.setCancelable(false);
		pd.setButton(DialogInterface.BUTTON_NEGATIVE,
				getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						h.removeCallbacksAndMessages(null);
					}
				});
		pd.show();
		isActiveDialog = true;
		h.sendEmptyMessageDelayed(1, 100);
	}

	public void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "training fragment onPause");
		sp.edit().putLong(START_TIME, startTime).apply();
		timerHandler.removeCallbacks(timerRunnable);
		saveSetsToPreferences();
		sp.edit().putInt(TOTAL_WEIGHT, total).apply();
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

				if (tmpCursor.getString(3).equals(date)) {
					tmpCursor.moveToPrevious();
				}

				Intent intent_history_detailed = new Intent(getActivity(),
						HistoryDetailedActivity.class);
				intent_history_detailed
						.putExtra("date", tmpCursor.getString(3));
				intent_history_detailed.putExtra("trName",
						tmpCursor.getString(1));
				startActivity(intent_history_detailed);
				tmpCursor.close();

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

			alExersicesList.add(db.getExerciseByID((int) itemsChecked[i]));
			alSetList.add(0);
		}
		for (int j = 0; j < 100; j++) {
			alSetList.add(0);
		}
		adapter.notifyDataSetChanged();
		String[] tmp = new String[alExersicesList.size()];

		for (int i = 0; i < alExersicesList.size(); i++) {
			tmp[i] = alExersicesList.get(i);
		}
		db.updateRec_Training(trainingId, 2, db.convertArrayToString(tmp));

	}

	private void updateTimer(String tmp) {
		String timerv;
		if (tmp != null) {
			timerv = tmp;
		} else {
			timerv = etTimer.getText().toString();
		}
		String tmpStr = db.getTimerValueByExerciseName(exeName);

		if (tmpStr != null && timerv != null && !tmpStr.equals(timerv)) {
			int exe_id = db.getExeIdByName(exeName);
			db.updateRec_Exe(exe_id, DB.TIMER_VALUE, timerv);
		}
		try {
			timerValue = Integer.parseInt(timerv);
		} catch (Exception e) {
			timerValue = 60;
		}

	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if (blocked) {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.select_an_exercise),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (id == R.id.llBtnSave && currentSet == set) {
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem() + 1);
			int tmp = alSetList.get(checkedPosition);
			tmp++;
			alSetList.set(checkedPosition, tmp);
			set = alSetList.get(checkedPosition);
			total = wei * rep_s;
			total += sp.getInt(BasicMenuActivityNew.TOTAL_WEIGHT, 0);
			sp.edit().putInt(BasicMenuActivityNew.TOTAL_WEIGHT, total).apply();
			db.addRecMainTable(traName, exeName, date, wei, rep_s, set);
			currentSet = set;
			initSetButtons();
			Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT)
					.show();
			oldReps = db.getLastWeightOrReps(exeName, set, false);
			oldWeight = db.getLastWeightOrReps(exeName, set, true);
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
			if (isActiveDialog) {
				h.sendEmptyMessage(0);
			}
			if (tglChecked) {
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
			onSelected(checkedPosition);
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
				onSelected(checkedPosition);
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
			minutes = (seconds / 60);
			seconds = (seconds % 60);
			bar.setSubtitle((String.format("%d:%02d", minutes, seconds)) + " "
					+ total + " " + measureItem + " " + " ["
					+ ((set == currentSet ? set : currentSet) + 1) + " "
					+ getResources().getString(R.string.set) + "] ");
			timerHandler.postDelayed(this, 500);
		}
	};

	public void saveSetsToPreferences() {

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < alSetList.size(); i++) {
			str.append(alSetList.get(i)).append(",");
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
			alSetList = array;
		}
	}

	private void playSound(Context context, Uri sound) {
		Log.d(LOG_TAG, "playing: " + sound.toString());
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, sound);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (Exception e) {
			System.out.println("OOPS");
			System.out.println(e.getMessage());
		}
	}

}