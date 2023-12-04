package com.example.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.animation.LinearInterpolator;
import com.example.mvvm.databinding.ActivitySplashScreenBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.datalocal.MyApplication;
import com.example.mvvm.function.AddFilmsActivity;
import com.example.mvvm.function.AddFilmsVM;
import com.example.mvvm.function.AddSeriesFilmsActivity;
import com.example.mvvm.function.DetailTicketActivity;
import com.example.mvvm.function.FastfoodActivity;
import com.example.mvvm.function.FilmsActivity;
import com.example.mvvm.function.ScanQRCodeActivity;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.function.UsersStatusActivity;
import com.example.mvvm.function.WalletActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_screen);

        if (!DataLocalManager.getFirstInstalled()){
            // xoay 360^ trong 2.107s
            binding.imgsplash.animate().rotationBy(360).setDuration(2107).setInterpolator(new LinearInterpolator()).start();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2107);
            DataLocalManager.setFirstInstalled(true);
        }else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}