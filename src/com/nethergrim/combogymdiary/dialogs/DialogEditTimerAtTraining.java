package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class DialogEditTimerAtTraining extends DialogFragment implements
		OnClickListener {

	private int timerValue = 0;
	public final static String TIMER_VALUE = "timer_value";
	private DB db;
	private EditText etMain;
	private OnTimerValueChanged listener;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		db = new DB(getActivity());
		db.open();
		AlertDialog.Builder adb = null;
		Bundle args = getArguments();
		if (args != null) {
			timerValue = args.getInt(TIMER_VALUE);
		}

		LayoutInflater ltInflater = getActivity().getLayoutInflater();

		View v = ltInflater.inflate(R.layout.dialog_edit_timer_value, null);
		adb = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.edit_timer_value)
				.setPositiveButton(R.string.yes, this)
				.setNegativeButton(R.string.no, this).setView(v);
		etMain = (EditText) v.findViewById(R.id.etTimer);
		etMain.setText("" + timerValue);
		return adb.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case Dialog.BUTTON_POSITIVE:
			
			if (!etMain.getText().toString().equals("")){
				listener.onTimerValueChanged(etMain.getText().toString());
				dismiss();
			}			
			break;
		case Dialog.BUTTON_NEGATIVE:
			dismiss();
			break;
		}
	}

	public static interface OnTimerValueChanged {
		public void onTimerValueChanged(String value);
	}

	@Override
	public void onAttach(Activity activity) {
		listener = (OnTimerValueChanged) activity;
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		listener = null;
		super.onDetach();
	}

}
