package com.example.mvvm.datalocal;

import android.content.Context;

public class DataLocalManager {
    private static final String FIRST_INSTALL = "FIRST_INSTALL";
    private static final String ADMIN = "ADMIN";
    private static final String NIGHT_MODE = "NIGHT_MODE";
    private static final String UID = "UID";
    private static DataLocalManager instance;
    private DataLocalPreferences dataLocalPreferences;

    public static void initialize(Context context){
        instance = new DataLocalManager();
        instance.dataLocalPreferences = new DataLocalPreferences(context);
    }

    public static DataLocalManager getInstance(){
        if (instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setFirstInstalled(boolean isfirst){
        DataLocalManager.getInstance().dataLocalPreferences.putBooleanValue(FIRST_INSTALL,isfirst);
    }

    public static boolean getAdmin(){
        return DataLocalManager.getInstance().dataLocalPreferences.getBooleanValue(ADMIN);
    }

    public static void setAdmin(boolean isadmin){
        DataLocalManager.getInstance().dataLocalPreferences.putBooleanValue(ADMIN,isadmin);
    }

    public static boolean getFirstInstalled(){
        return DataLocalManager.getInstance().dataLocalPreferences.getBooleanValue(FIRST_INSTALL);
    }

    public static void setNightMode(boolean nightMode){
        DataLocalManager.getInstance().dataLocalPreferences.putBooleanValue(NIGHT_MODE,nightMode);
    }

    public static boolean getNightMode(){
        return DataLocalManager.getInstance().dataLocalPreferences.getBooleanValue(NIGHT_MODE);
    }

    public static void setUid(String idUser){
        DataLocalManager.getInstance().dataLocalPreferences.putStringValue(UID,idUser);
    }

    public static String getUid(){
        return DataLocalManager.getInstance().dataLocalPreferences.getStringValue(UID);
    }
}
