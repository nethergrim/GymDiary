package com.nethergrim.combogymdiary.fragments;

import java.util.ArrayList;

import kankan.wheel.widget.WheelView;

import com.mobeta.android.dslv.DragSortListView;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining.MyInterface;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TrainingFragment extends Fragment implements MyInterface,
		OnCheckedChangeListener, OnClickListener {

	public final static String TRAINING_AT_PROGRESS = "training_at_progress";
	public final static String TRAINING_NAME = "training_name";
	public final static String TRA_ID = "training_id";
	public final static String TRAINING_LIST = "training_list";
	public final static String TIMER_IS_ON = "timerIsOn";
	private ToggleButton tglTimerOn;
	private Boolean tglChecked = true, turnOff = false, vibrate = false;
	private EditText etTimer;
	private DB db;
	private Cursor cursor;
	private int trainingIdAtTable = 0;
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
	private boolean isTrainingProgress = false;
	private Handler timerHandler = new Handler();
	private LinearLayout llBack, llSave, llForward, llBottom, llTimerProgress;
	private ImageView ivBack, ivForward;
	private Animation anim = null;
	private DragSortListView list;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		db = new DB(getActivity());
		db.open();
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		isTrainingProgress = sp.getBoolean(TRAINING_AT_PROGRESS, false);
		int training_id = 0;
		
		if (isTrainingProgress) {
			traName = sp.getString(TRAINING_NAME, "");
			String[] strArrExtra = { traName };
			cursor = db.getDataTrainings(null, DB.TRA_NAME + "=?", strArrExtra,
					null, null, null);
		} else {
			training_id = getIntent().getIntExtra(TRA_ID, 0);
			String[] args = { String.valueOf(training_id) };
			cursor = db.getDataTrainings(null, DB.COLUMN_ID + "=?", args, null,
					null, null);
			if (cursor.moveToFirst()) {
				traName = cursor.getString(1);
			}
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.training_at_progress_new_wheel_new_list, null);

		return v;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

	}

	@Override
	public void onChoose() {

	}

}
