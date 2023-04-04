package com.example.mvvm.datalocal;

import android.content.Context;
import android.content.SharedPreferences;

public class DataLocalPreferences {
    private static final String DATA_LOCAL_PREFERENCES ="DATA_LOCAL_PREFERENCES";
    private Context context;

    public DataLocalPreferences(Context context) {
        this.context = context;
    }

    public void putBooleanValue(String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_LOCAL_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public boolean getBooleanValue(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_LOCAL_PREFERENCES,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

    public void putStringValue(String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_LOCAL_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getStringValue(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATA_LOCAL_PREFERENCES,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
}
