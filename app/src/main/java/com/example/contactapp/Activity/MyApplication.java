package com.example.contactapp.Activity;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }
}
