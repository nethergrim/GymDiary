package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.nethergrim.combogymdiary.R;

public class DialogRestoreFromBackup extends DialogFragment implements
		OnClickListener {

	final String LOG_TAG = "myLogs";

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.attention)
				.setPositiveButton(R.string.yes, this)
				.setNegativeButton(R.string.no, this)
				.setMessage(R.string.restoreAreYouSure);
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		int i = 0;
		switch (which) {
		case Dialog.BUTTON_POSITIVE:
			mListener.onChoose();
			break;
		case Dialog.BUTTON_NEGATIVE:
			break;
		}
		if (i > 0)
			Log.d(LOG_TAG, "Dialog 2: " + getResources().getString(i));
	}

	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		Log.d(LOG_TAG, "Dialog 2: onCancel");
	}

	public static interface MyInterface {
		public void onChoose();
	}

	private MyInterface mListener;

	@Override
	public void onAttach(Activity activity) {
		mListener = (MyInterface) activity;
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}
}