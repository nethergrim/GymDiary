package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;

public class DialogAddCommentToTraining extends DialogFragment implements
        OnClickListener {

    private SharedPreferences sp;
    private EditText et;
    private Button btn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.add_comment_to_training);
        View v = inflater
                .inflate(R.layout.dialog_add_comment_to_training, null);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        et = (EditText) v.findViewById(R.id.editText1AddComment);
        btn = (Button) v.findViewById(R.id.buttonSave);
        btn.setOnClickListener(this);
        if (!sp.getString(BasicMenuActivityNew.COMMENT_TO_TRAINING, "").equals(
                "")) {
            et.setText(sp.getString(BasicMenuActivityNew.COMMENT_TO_TRAINING,
                    ""));
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSave) {
            sp.edit()
                    .putString(BasicMenuActivityNew.COMMENT_TO_TRAINING,
                            et.getText().toString()).apply();
            dismiss();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
