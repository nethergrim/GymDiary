package com.nethergrim.combogymdiary.activities;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddingExersisesActivity extends Activity implements
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adding_exersise);
		getActionBar().setTitle(R.string.create_new_exercise);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		btnCreate = (Button) findViewById(R.id.btnSave);
		btnCreate.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etTimerValue);
		etTimer = (EditText) findViewById(R.id.editText2);

		db = new DB(this);
		db.open();

		Intent intent = getIntent();
		exeName = intent.getStringExtra("exeName");
		timerV = intent.getStringExtra("timerValue");
		exeID = intent.getLongExtra("exeID", 0);
		if (exeName != null && timerV != null) {
			editOrNot = true;
		}
		if (editOrNot) {
			etName.setText(exeName);
			etTimer.setText(timerV);
		}
		sp = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			return true;
		}
		return false;
	}

	protected void onResume() {
		defaultTimer = sp.getString("etDefault", "60");
		etTimer.setText(defaultTimer);
		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		String name = etName.getText().toString();
		String timer = etTimer.getText().toString();

		int id = arg0.getId();
		if (id == R.id.btnSave && editOrNot == false) {
			if (!name.isEmpty() && !timer.isEmpty()) {
				db.addRec_Exe(name, timer);
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if (id == R.id.btnSave && editOrNot == true) {
			if (!name.isEmpty() && !timer.isEmpty()) {
				db.updateRec_Exe((int) exeID, DB.EXE_NAME, name);
				db.updateRec_Exe((int) exeID, DB.TIMER_VALUE, timer);
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
}
