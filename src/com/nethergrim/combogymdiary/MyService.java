package com.nethergrim.combogymdiary;

import com.nethergrim.combogymdiary.activities.BasicMenuActivity;
import com.nethergrim.combogymdiary.activities.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class MyService extends Service {

	private NotificationManager nm;
	private SharedPreferences sp;
	private String trainingNAME;

	@Override
	public void onCreate() {
		super.onCreate();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		trainingNAME = sp.getString(BasicMenuActivity.TRAINING_NAME, "");
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		trainingNAME = sp.getString(BasicMenuActivity.TRAINING_NAME, "");
		sendNotif(startId);
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressWarnings("deprecation")
	void sendNotif(int ID) {

		Notification notif = new Notification(R.drawable.ic_launcher,
				getResources().getString(R.string.training_started),
				System.currentTimeMillis());

		Intent intent = new Intent(this, MainActivity.class);
		Editor ed = sp.edit();
		ed.putInt(BasicMenuActivity.TRA_ID, ID);
		ed.apply();

		intent.putExtra("trainingName", trainingNAME);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		notif.setLatestEventInfo(this,
				getResources().getString(R.string.finish_training), "", pIntent);

		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.FLAG_NO_CLEAR;

		nm.notify(1, notif);

	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
}