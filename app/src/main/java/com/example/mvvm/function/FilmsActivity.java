package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.databinding.ActivityFilmsBinding;
import com.example.mvvm.livedata.FilmsAllLiveData;
import com.example.mvvm.livedata.FilmsHotLiveData;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.livedata.FilmsOfflineLiveData;
import com.example.mvvm.livedata.FilmsOnlineLiveData;
import com.example.mvvm.livedata.SeriesFilmsLiveData;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FilmsActivity extends AppCompatActivity {
    ActivityFilmsBinding binding;
    FilmsVM filmsVM;
    FilmsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_films);
        filmsVM = new FilmsVM();
        binding.setListfilms(filmsVM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        binding.tablayout.addTab(binding.tablayout.newTab().setText("Tất cả"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim chiếu rạp"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim online"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim mới"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Phim nổi bật"));

        // setup RecycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerFilm.setLayoutManager(layoutManager);
        binding.recyclerFilm.setHasFixedSize(false);

        adapter = new FilmsAdapter(new ArrayList<String>());
        binding.recyclerFilm.setAdapter(adapter);

        //
        TabLayout.Tab tab = binding.tablayout.getTabAt(0);
        if (tab != null) {
            String title = (String) tab.getText();
            loadAdapter(title);
        }

        // Bắt sự kiện khi người dùng nhấn vào một tab
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                binding.recyclerFilm.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);

                // Được gọi khi một tab được chọn
                String tabTitle = tab.getText().toString();

                // Hiển thị RecycleView theo title của tab
//                Toast.makeText(getApplicationContext(), tabTitle, Toast.LENGTH_SHORT).show();
                loadAdapter(tabTitle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Được gọi khi một tab không còn được chọn nữa
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Được gọi khi một tab đã được chọn lại
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

    public void loadAdapter(String title) {
        if (title.equals("Phim mới")) {
            FilmsNewLiveData liveData = ViewModelProviders.of(this).get(FilmsNewLiveData.class);
            liveData.getLiveData().observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> key) {
                    if (key.size() != 0) {
                        binding.recyclerFilm.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    }
                    adapter.setFilmsAdapter(key);
                }
            });
        } else if (title.equals("Phim nổi bật")) {
            FilmsHotLiveData liveData = ViewModelProviders.of(this).get(FilmsHotLiveData.class);
            liveData.getLiveData().observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> key) {
                    if (key.size() != 0) {
                        binding.recyclerFilm.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    }
                    adapter.setFilmsAdapter(key);
                }
            });
        } else if (title.equals("Phim online")) {
            FilmsOnlineLiveData liveData = ViewModelProviders.of(this).get(FilmsOnlineLiveData.class);
            liveData.getLiveData().observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> key) {
                    if (key.size() != 0) {
                        binding.recyclerFilm.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    }
                    adapter.setFilmsAdapter(key);
                }
            });
        } else if (title.equals("Phim chiếu rạp")) {
            FilmsOfflineLiveData liveData = ViewModelProviders.of(this).get(FilmsOfflineLiveData.class);
            liveData.getLiveData().observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> key) {
                    if (key.size() != 0) {
                        binding.recyclerFilm.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    }
                    adapter.setFilmsAdapter(key);
                }
            });

        } else {
            FilmsAllLiveData liveData = ViewModelProviders.of(this).get(FilmsAllLiveData.class);
            liveData.getLiveData().observe(this, new Observer<List<String>>() {
                @Override
                public void onChanged(List<String> key) {
                    if (key.size() != 0) {
                        binding.recyclerFilm.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                    }
                    adapter.setFilmsAdapter(key);
                }
            });
        }
    }
}