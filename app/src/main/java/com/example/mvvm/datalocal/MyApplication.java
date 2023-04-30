package com.example.mvvm.datalocal;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DataLocalManager.init(getApplicationContext());
    }
    public static MyApplication getInstance(){
        return instance;
    }
}
