package com.nomaddeveloper.securai;

import android.app.Application;
import android.content.Context;

public class SecuraiApp extends Application {
    private static SecuraiApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static SecuraiApp getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
