package com.example.mvvm.fragment;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.adapter.SeriesFilmsAdapter;
import com.example.mvvm.databinding.ActivityMovieStoreFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class MovieStoreFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityMovieStoreFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_movie_store_fragment, container, false);
        MovieStoreVM viewModel = ViewModelProviders.of(this).get(MovieStoreVM.class);
        binding.setMoviestore(viewModel);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // setup RecycleView
        GridLayoutManager layoutManagerSeries = new GridLayoutManager(binding.recyclerviewA.getContext(), 3);
        binding.recyclerviewA.setLayoutManager(layoutManagerSeries);
        binding.recyclerviewA.setHasFixedSize(false);

        SeriesFilmsAdapter adapterSeries = new SeriesFilmsAdapter(new ArrayList<String>());
        binding.recyclerviewA.setAdapter(adapterSeries);

        // setup RecycleView
        GridLayoutManager layoutManagerPoster = new GridLayoutManager(binding.recyclerviewA.getContext(), 3);
        binding.recyclerviewB.setLayoutManager(layoutManagerPoster);
        binding.recyclerviewB.setHasFixedSize(false);

        PosterAdapter adapter = new PosterAdapter(new ArrayList<String>());
        binding.recyclerviewB.setAdapter(adapter);

        // Quan sát uniqueListLiveData từ ViewModel
        viewModel.getUniqueListLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> uniqueList) {
                // Cập nhật dữ liệu trong Adapter khi uniqueList thay đổi
                adapterSeries.setAdapter(uniqueList);
            }
        });

        // Quan sát notInBListLiveData từ ViewModel
        viewModel.getNotInBListLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> notInBList) {
                // Cập nhật dữ liệu trong Adapter khi notInBList thay đổi
                adapter.setPosterAdapter(notInBList);
            }
        });

        // Gọi fetchDataFromFirebase để lấy dữ liệu từ Firebase
        viewModel.fetchDataFromFirebase();
        return binding.getRoot();
    }
}