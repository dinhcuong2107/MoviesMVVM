package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.databinding.ActivityShowTimesBinding;
import com.example.mvvm.model.Films;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instacart.library.truetime.TrueTimeRx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.schedulers.Schedulers;

public class ShowTimesActivity extends AppCompatActivity {
    ArrayList title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityShowTimesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_show_times);
        binding.setShowtimes(new ShowTimesVM());
        binding.executePendingBindings();

        TrueTimeRx.build()
                .withLoggingEnabled(true)
                .withSharedPreferencesCache(this)
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> Log.v("TrueTime", "TrueTime initialized, time: " + date),
                        throwable -> Log.e("TrueTime", "TrueTime exception: ", throwable)
                );

        if (TrueTimeRx.isInitialized()) {
//            String time = new SimpleDateFormat("HH:mm").format(TrueTimeRx.now());
//            String date = new SimpleDateFormat("dd/MM/yyyy").format(TrueTimeRx.now());
            title = new ArrayList();
            for (int i=0; i<7; i++)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(TrueTimeRx.now());
                calendar.add(Calendar.DAY_OF_YEAR, i);
                String date = new SimpleDateFormat("EEEE\ndd/MM/yyyy").format(calendar.getTime());
                binding.tablayout.addTab(binding.tablayout.newTab().setText(date));
                title.add(date);
            }
        }
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(ShowTimesActivity.this, ""+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}