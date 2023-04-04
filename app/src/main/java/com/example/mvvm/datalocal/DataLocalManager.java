package com.example.mvvm.datalocal;

import android.content.Context;

public class DataLocalManager {
    private static final String FIRST_INSTALL = "FIRST_INSTALL";
    private static final String UID = "UID";
    private static DataLocalManager instance;
    private DataLocalPreferences dataLocalPreferences;

    public static void init(Context context){
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

    public static boolean getFirstInstalled(){
        return DataLocalManager.getInstance().dataLocalPreferences.getBooleanValue(FIRST_INSTALL);
    }

    public static void setUid(String idUser){
        DataLocalManager.getInstance().dataLocalPreferences.putStringValue(UID,idUser);
    }

    public static String getUid(){
        return DataLocalManager.getInstance().dataLocalPreferences.getStringValue(UID);
    }
}
