package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mvvm.Functions;
import com.example.mvvm.R;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.adapter.ShowTimesAdapter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class ShowTimesActivity extends AppCompatActivity {
    ArrayList title;
    String now_title="";
    ActivityShowTimesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_times);
        binding.setShowtimes(new ShowTimesVM());
        binding.executePendingBindings();

        now_title = new SimpleDateFormat("dd/MM/yyyy").format(Functions.getRealtime());
        setupRecycleView();
        title = new ArrayList();
        for (int i=0; i<7; i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Functions.getRealtime());
            calendar.add(Calendar.DAY_OF_YEAR, i);
            String date = new SimpleDateFormat("EEEE\ndd/MM/yyyy").format(calendar.getTime());
            binding.tablayout.addTab(binding.tablayout.newTab().setText(date));
            title.add(date);
        }

        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                now_title = tab.getText().toString().substring(tab.getText().toString().length()-10);
                setupRecycleView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerview.setLayoutManager(layoutManager);

        List<String> time = Arrays.asList(getResources().getStringArray(R.array.timemovies));
        ShowTimesAdapter adapter = new ShowTimesAdapter(time, now_title);
        adapter.notifyDataSetChanged();
        binding.recyclerview.setAdapter(adapter);
    }
}