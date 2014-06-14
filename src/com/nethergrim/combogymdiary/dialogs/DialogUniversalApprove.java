package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;

public class DialogUniversalApprove extends DialogFragment implements
        OnClickListener {

    private int type_of_dialog = 0;
    private int tra_id = 0;
    private long id = 0;
    private int pos = 0;
    private DB db;
    private OnEditExerciseAccept listener;
    private OnStartTrainingAccept mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = new DB(getActivity());
        db.open();
        AlertDialog.Builder adb = null;
        Bundle args = getArguments();
        if (args != null) {
            type_of_dialog = args.getInt(BasicMenuActivityNew.TYPE_OF_DIALOG);
        }
        if (type_of_dialog == 0) {
            tra_id = args.getInt(BasicMenuActivityNew.ID);
            String tra_name = db.getTrainingName(tra_id);

            adb = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.start_training)
                    .setPositiveButton(R.string.yes, this)
                    .setNegativeButton(R.string.no, this)
                    .setMessage(
                            getResources().getString(R.string.start_training)
                                    + ": " + tra_name + " ?"
                    );
        } else if (type_of_dialog == 1) {
            id = args.getLong(BasicMenuActivityNew.ID);
            pos = args.getInt(BasicMenuActivityNew.POSITION);
            String exe_name = db.getExerciseByID((int) id);

            adb = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.edit_exercise)
                    .setPositiveButton(R.string.yes, this)
                    .setNegativeButton(R.string.no, this)
                    .setMessage(
                            getResources().getString(R.string.edit_exercise)
                                    + ": " + exe_name + " ?"
                    );

        } else
            dismiss();

        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (type_of_dialog == 0) {
                    mListener.onAccept(tra_id);
                } else if (type_of_dialog == 1) {
                    listener.onAcceptEditExercise(id, pos);
                }
                dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        mListener = (OnStartTrainingAccept) activity;
        listener = (OnEditExerciseAccept) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mListener = null;
        listener = null;
        super.onDetach();
    }

    public static interface OnEditExerciseAccept {
        public void onAcceptEditExercise(long id, int pos);
    }

    public static interface OnStartTrainingAccept {
        public void onAccept(int trainingId);
    }

}
