package com.example.mvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.mvvm.adapter.ViewPagerAdapter;
import com.example.mvvm.databinding.ActivityMainBinding;
import com.example.mvvm.databinding.ActivitySplashScreenBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.effect.DepthPageTransformer;
import com.example.mvvm.function.AddFilmsActivity;
import com.example.mvvm.model.Users;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setMain(new MainVM());
        binding.executePendingBindings();

        //click
        binding.navigationMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_home: binding.viewpager.setCurrentItem(0);break;
                    case R.id.item_ticketstore: binding.viewpager.setCurrentItem(1);break;
                    case R.id.item_moviestore: binding.viewpager.setCurrentItem(2);break;
                    case R.id.item_setting: binding.viewpager.setCurrentItem(3);break;
                }
                return true;
            }
        });

        // load viewpager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewpager.setAdapter(viewPagerAdapter);
        binding.viewpager.setPageTransformer(new DepthPageTransformer());
        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0: binding.navigationMain.getMenu().findItem(R.id.item_home).setChecked(true);break;
                    case 1: binding.navigationMain.getMenu().findItem(R.id.item_ticketstore).setChecked(true);break;
                    case 2: binding.navigationMain.getMenu().findItem(R.id.item_moviestore).setChecked(true);break;
                    case 3: binding.navigationMain.getMenu().findItem(R.id.item_setting).setChecked(true);break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn Back lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();

        handler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // Thiết lập thời gian cho phép nhấn lần thứ hai
    }
}