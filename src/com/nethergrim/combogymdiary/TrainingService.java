package com.nethergrim.combogymdiary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.nethergrim.combogymdiary.activities.StartActivity;

public class TrainingService extends Service {

    // private String trainingNAME;
    // private final static String TRAINING_NAME = "training_name";
    private final static String TRA_ID = "training_id";
    private NotificationManager nm;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        // trainingNAME = sp.getString(TRAINING_NAME, "");
        sp.edit().putInt(TRA_ID, startId).apply();
        sendNotif(startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    void sendNotif(int ID) {

        Notification notif = new Notification(R.drawable.ic_launcher,
                getResources().getString(R.string.training_started),
                System.currentTimeMillis());

        Intent intent = new Intent(this, StartActivity.class);
        // intent.putExtra("trainingName", trainingNAME);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notif.setLatestEventInfo(this,
                getResources().getString(R.string.finish_training), "", pIntent);

        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.flags |= Notification.FLAG_ONGOING_EVENT;

        nm.notify(1, notif);

    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}