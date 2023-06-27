package com.example.mvvm.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityMovieStoreFragmentBinding;

public class MovieStoreFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityMovieStoreFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_movie_store_fragment, container, false);
        binding.setMoviestore(new MovieStoreVM());
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        return binding.getRoot();
    }
}