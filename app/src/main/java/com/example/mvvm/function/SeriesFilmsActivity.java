package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.adapter.SeriesFilmsAdapter;
import com.example.mvvm.databinding.ActivityAddSeriesFilmsBinding;
import com.example.mvvm.databinding.ActivitySeriesFilmsBinding;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.livedata.SeriesFilmsLiveData;

import java.util.ArrayList;
import java.util.List;

public class SeriesFilmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySeriesFilmsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_series_films);
        SeriesFilmsVM seriesFilmsVM =  ViewModelProviders.of(this).get(SeriesFilmsVM.class);
        binding.setSeries(seriesFilmsVM);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        // setup RecycleView
        GridLayoutManager layoutManager = new GridLayoutManager(binding.recyclerview.getContext(), 4);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(false);

        SeriesFilmsAdapter adapter = new SeriesFilmsAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(adapter);

        SeriesFilmsLiveData liveData = ViewModelProviders.of(this).get(SeriesFilmsLiveData.class);
        liveData.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                if (key.size() != 0) {
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    binding.emptyView.setVisibility(View.GONE);
                }
                adapter.setAdapter(key);
            }
        });


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