package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.adapter.UsersAdapter;
import com.example.mvvm.databinding.ActivityUsersStatusBinding;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.model.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersStatusActivity extends AppCompatActivity {
    ActivityUsersStatusBinding binding;
    UsersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_users_status);
        UsersStatusVM viewmodel = ViewModelProviders.of(this).get(UsersStatusVM.class);
        binding.setUsersVM(viewmodel);
        binding.executePendingBindings();

        // setup RecycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerUsers.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerUsers.setLayoutManager(layoutManager);
        binding.recyclerUsers.setHasFixedSize(false);

        adapter = new UsersAdapter(new ArrayList<String>());
        binding.recyclerUsers.setAdapter(adapter);

        viewmodel.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setUsersAdapter(key);
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

//    @Override
//    public void onBackPressed() {
//        if (!binding.searchView.isIconified()){
//            binding.searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }
}