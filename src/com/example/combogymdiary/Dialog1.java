package com.example.combogymdiary;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Dialog1 extends DialogFragment implements OnClickListener {

  final String LOG_TAG = "myLogs";

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    getDialog().setTitle(R.string.save_and_exit);
    View v = inflater.inflate(R.layout.dialog_exit, null);
    v.findViewById(R.id.btnYes).setOnClickListener(this);
    v.findViewById(R.id.btnNo).setOnClickListener(this);
    return v;
  }

  public void onClick(View v) {
    int ID = ((Button)v).getId();
    if (ID == R.id.btnYes){
    	Context c = MainActivity.ma.getApplicationContext();
    	Intent gotoMain = new Intent(c,MainActivity.class);
		startActivity(gotoMain);
    }
    dismiss();
  }

  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
  }

  public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);
  }
}