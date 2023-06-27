package com.example.mvvm;

import android.util.Log;
import com.example.mvvm.datalocal.MyApplication;
import com.instacart.library.truetime.TrueTimeRx;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class Functions {

    public static Date getRealtime() {
        TrueTimeRx.build()
                .withLoggingEnabled(true)
                .withSharedPreferencesCache(MyApplication.getInstance())
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> Log.v("TrueTime", "TrueTime initialized, time: " + date),
                        throwable -> Log.e("TrueTime", "TrueTime exception: ", throwable)
                );
        return TrueTimeRx.now();
    }
    public static Boolean isNumber(String string){
        for (char c : string.toCharArray()){
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    public static String convertPriceToVND(int price){
        DecimalFormat formater = new DecimalFormat("#,### VNĐ");
        String output = formater.format(price);
        return output;
    }
    public static Integer convertVNDToPrice(String price){
        price = price.replace(" VNĐ", "");
        DecimalFormat formater = new DecimalFormat("#,###");
        int number = 0;
        try {
            number = formater.parse(price).intValue();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return number;
    }

    public static List<String> convertStringToList(String string){
        string = string.replace("[", "").replace("]", "").replace(" ","");
        List<String> list = new ArrayList<>(Arrays.asList(string.split(",")));
        return list;
    }

    public static Integer convertSeat(String seat) {
        String first = seat.substring(0,1);
        int last;
        if (seat.length()==2)
        {
            last = Integer.parseInt(seat.substring(seat.length() - 1));
        }else {
            last = Integer.parseInt(seat.substring(seat.length() - 2));
        }

        if (first.equals("B")) {
            return last + 12 -1;
        }
        if (first.equals("C")) {
            return last + 12 * 2 -1;
        }
        if (first.equals("D")) {
            return last + 12 * 3 -1;
        }
        if (first.equals("E")) {
            return last + 12 * 4 -1;
        }
        if (first.equals("F")) {
            return last + 12 * 5 -1;
        }
        if (first.equals("G")) {
            return last + 12 * 6 -1;
        }
        if (first.equals("H")) {
            return last + 12 * 6 +8 -1;
        }
        return last-1;
    }

}
