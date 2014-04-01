package com.nethergrim.combogymdiary.dialogs;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class DialogUniversalApprove extends DialogFragment implements
		OnClickListener {
	
	private int type_of_dialog = 0;
	private int tra_id = 0;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adb = null;
		Bundle args = getArguments();
		if (args != null) {
			type_of_dialog = args.getInt(BasicMenuActivityNew.TYPE_OF_DIALOG);			
		}
		if (type_of_dialog == 1){
			tra_id = args.getInt(BasicMenuActivityNew.ID);
			String tra_name = new DB(getActivity()).getTrainingNameById(tra_id);
			adb = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.start_training) 
			.setPositiveButton(R.string.yes, this)
			.setNegativeButton(R.string.no, this)
			.setMessage(R.string.start_training + ": " + tra_name + " ?");
		} else dismiss();
		 
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which) {		
		switch (which) {
		case Dialog.BUTTON_POSITIVE: 
			if (type_of_dialog == 1){
				mListener.onAccept(tra_id);
			}
			break;
		case Dialog.BUTTON_NEGATIVE:
			break;
		}

	}
	
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);		
	}
	
	public static interface OnStartTrainingAccept {
		public void onAccept(int trainingId);
	}

	private OnStartTrainingAccept mListener;
	
	@Override
	public void onAttach(Activity activity) {
		mListener = (OnStartTrainingAccept) activity;
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}

}
