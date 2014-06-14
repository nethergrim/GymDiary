package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nethergrim.combogymdiary.R;

public class DialogInfo extends DialogFragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.info);
        View v = inflater.inflate(R.layout.dialog_info, null);
        return v;
    }


}
