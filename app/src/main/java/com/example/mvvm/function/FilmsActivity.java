package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FastfoodAdapter;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.adapter.ShowTimesAdapter;
import com.example.mvvm.databinding.ActivityFilmsBinding;
import com.example.mvvm.livedata.FastfoodLiveData;
import com.example.mvvm.livedata.FilmsAllLiveData;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.model.Films;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilmsActivity extends AppCompatActivity {
    FilmsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFilmsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_films);
        FilmsVM filmsVM = new FilmsVM();
        binding.setListfilms(filmsVM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        binding.tablayout.addTab(binding.tablayout.newTab().setText("Tất cả"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim chiếu rạp"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim online"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim mới"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim nổi bật"));

        // setup RecycleView Fastfood
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerFilm.setLayoutManager(layoutManager);
        binding.recyclerFilm.setHasFixedSize(false);

        adapter = new FilmsAdapter(new ArrayList<String>());
        binding.recyclerFilm.setAdapter(adapter);

        TabLayout.Tab tab = binding.tablayout.getTabAt(0);
        if (tab != null){
            CharSequence title = tab.getText();
            if (title.equals("Tất cả")){
                FilmsAllLiveData liveData = ViewModelProviders.of(this).get(FilmsAllLiveData.class);
                liveData.getLiveData().observe(this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> key) {
                        adapter.setFilmsAdapter(key);
                    }
                });
            }else {
                Toast.makeText(this, ""+title, Toast.LENGTH_SHORT).show();
            }
        }

        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}