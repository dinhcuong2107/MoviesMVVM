package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mvvm.Utils;
import com.example.mvvm.R;
import com.example.mvvm.adapter.ShowTimesAdapter;
import com.example.mvvm.databinding.ActivityShowTimesBinding;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowTimesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityShowTimesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_show_times);
        ShowTimesVM showTimesVM = new ShowTimesVM();
        showTimesVM.loadData();
        binding.setShowtimes(showTimesVM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();
        showTimesVM.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                for ( int i = 0; i < 7; i++){
                    Log.d(TAG, "onChanged: " + list.get(i));
                    binding.tablayout.addTab(binding.tablayout.newTab().setText(list.get(i)));
                }
            }
        });

        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String now_title = tab.getText().toString().substring(tab.getText().toString().length()-10);

                //setupRecycleView
                LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL,false);
                binding.recyclerview.setLayoutManager(layoutManager);

                List<String> time = Arrays.asList(getResources().getStringArray(R.array.timemovies));
                ShowTimesAdapter adapter = new ShowTimesAdapter(time, now_title);
                adapter.notifyDataSetChanged();
                binding.recyclerview.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}