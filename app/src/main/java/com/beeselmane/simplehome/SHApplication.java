package com.beeselmane.simplehome;

import android.app.Application;
import android.content.Context;

public class SHApplication extends Application {
    private static volatile Context appContext = null;

    @Override
    public void onCreate() {
        super.onCreate();

        SHApplication.appContext = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return SHApplication.appContext;
    }
}
