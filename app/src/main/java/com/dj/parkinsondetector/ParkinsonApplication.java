package com.dj.parkinsondetector;

import android.app.Application;
import android.content.Context;

public class ParkinsonApplication extends Application {
    private static ParkinsonApplication ourInstance ;

    public static ParkinsonApplication getInstance() {
        return ourInstance;
    }

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        context = getApplicationContext();
    }
    public Context getContext() {
        return context;
    }
}
