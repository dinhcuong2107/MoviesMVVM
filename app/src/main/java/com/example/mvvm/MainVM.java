package com.example.mvvm;

import android.util.Log;

import androidx.databinding.BaseObservable;

import com.example.mvvm.datalocal.MyApplication;
import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class MainVM extends BaseObservable{

    public static class Functions {

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

    }
}