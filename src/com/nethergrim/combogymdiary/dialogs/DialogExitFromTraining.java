package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nethergrim.combogymdiary.R;

public class DialogExitFromTraining extends DialogFragment implements
        OnClickListener {

    final String LOG_TAG = "myLogs";
    private MyInterface mListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.save_and_exit);
        View v = inflater.inflate(R.layout.dialog_exit, null);
        v.findViewById(R.id.btnYes).setOnClickListener(this);
        v.findViewById(R.id.btnNo).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        int ID = ((Button) v).getId();
        if (ID == R.id.btnYes) {
            mListener.onChoose();
        }
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

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

    public static interface MyInterface {
        public void onChoose();
    }
}
