package com.nethergrim.combogymdiary;

import com.yandex.metrica.Counter;

import android.app.Application;

public class MyApp extends Application {
	  
	  @Override
	  public void onCreate() {
	    super.onCreate();
	    Counter.initialize(getApplicationContext());
	    Counter.sharedInstance().setTrackLocationEnabled(false);
	    }
	  
	}
