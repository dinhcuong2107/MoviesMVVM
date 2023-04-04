package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.databinding.ActivityFilmsBinding;
import com.example.mvvm.livedata.FilmsOfflineVM;
import com.example.mvvm.livedata.FilmsOnlineVM;
import com.example.mvvm.model.Films;

import java.util.List;

public class FilmsActivity extends AppCompatActivity {
    FilmsAdapter adapterOFF,adapterON;
    ActivityFilmsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_films);
        binding.setListfilms(new FilmsVM(false));
        binding.setLifecycleOwner(this);

        // setup RecycleView Phim Offline
        LinearLayoutManager layoutManagerOff = new LinearLayoutManager(binding.recyclerFilmOff.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerFilmOff.setLayoutManager(layoutManagerOff);
        FilmsOfflineVM filmsOfflineVM = new ViewModelProvider(this).get(FilmsOfflineVM.class);
        filmsOfflineVM.getLiveData().observe(this, new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                adapterOFF = new FilmsAdapter(films);
                binding.recyclerFilmOff.setAdapter(adapterOFF);
            }
        });

        // setup RecycleView Phim Online
        LinearLayoutManager layoutManagerOn = new LinearLayoutManager(binding.recyclerFilmOn.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerFilmOn.setLayoutManager(layoutManagerOn);
        FilmsOnlineVM filmsOnlineVM = new ViewModelProvider(this).get(FilmsOnlineVM.class);
        filmsOnlineVM.getLiveData().observe(this, new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                adapterON = new FilmsAdapter(films);
                binding.recyclerFilmOn.setAdapter(adapterON);
            }
        });
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterON.getFilter().filter(query);
                adapterOFF.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterON.getFilter().filter(newText);
                adapterOFF.getFilter().filter(newText);
                return false;
            }
        });
    }
}