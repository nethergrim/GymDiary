package com.nethergrim.combogymdiary;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

@ReportsCrashes(
    formKey = "", // This is required for backward compatibility but not used
    mailTo = "c2q9450@gmail.com", // my email here
    mode = ReportingInteractionMode.TOAST,
    resToastText = R.string.acra_error
)
public class MyApplication extends Application {
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate() {
        super.onCreate();
        
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        ErrorReporter.getInstance().checkReportsOnApplicationStart();
    }
}