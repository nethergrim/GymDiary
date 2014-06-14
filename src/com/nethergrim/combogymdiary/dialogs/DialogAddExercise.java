package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

public class DialogAddExercise extends DialogFragment implements
        OnClickListener {

    private Button btnCreate;
    private EditText etName, etTimer;
    private String exeName = "", timerV = "";
    private long exeID = 0;
    private String defaultTimer;
    private Boolean editOrNot = false;
    private DB db;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            editOrNot = true;
            exeName = args.getString("exeName");
            timerV = args.getString("timerValue");
            exeID = args.getLong("exeID");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.add_an_exercise);
        View v = inflater.inflate(R.layout.adding_exersise, null);
        btnCreate = (Button) v.findViewById(R.id.btnSave);
        btnCreate.setOnClickListener(this);
        etName = (EditText) v.findViewById(R.id.etTimerValue);
        etTimer = (EditText) v.findViewById(R.id.editText2);
        db = new DB(getActivity());
        db.open();

        if (editOrNot) {
            etName.setText(exeName);
            etTimer.setText(timerV);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return v;
    }

    public void onClick(View v) {
        String name = etName.getText().toString();
        String timer = etTimer.getText().toString();

        int id = v.getId();
        if (id == R.id.btnSave && editOrNot == false) {
            if (!name.isEmpty() && !timer.isEmpty()) {
                db.addRecExe(name, timer);
                Toast.makeText(getActivity(), R.string.saved,
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        } else if (id == R.id.btnSave && editOrNot == true) {
            if (!name.isEmpty() && !timer.isEmpty()) {
                db.updateRec_Exe((int) exeID, DB.EXE_NAME, name);
                db.updateRec_Exe((int) exeID, DB.TIMER_VALUE, timer);
                Toast.makeText(getActivity(), R.string.saved,
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
        ((FragmentActivity) getActivity()).getSupportLoaderManager()
                .getLoader(1).forceLoad();

    }

    public void onResume() {
        super.onResume();
        defaultTimer = sp.getString("etDefault", "60");
        etTimer.setText(defaultTimer);

    }
}
