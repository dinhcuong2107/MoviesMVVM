package com.example.mvvm.fragment;

import static androidx.databinding.DataBindingUtil.setContentView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FastfoodAdapter;
import com.example.mvvm.adapter.TicketAdapter;
import com.example.mvvm.databinding.ActivityTicketStoreFragmentBinding;
import com.example.mvvm.livedata.FastfoodLiveData;

import java.util.ArrayList;
import java.util.List;

public class TicketStoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityTicketStoreFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_ticket_store_fragment,container,false);
        TicketStoreVM viewmodel = new ViewModelProvider(this).get(TicketStoreVM.class);
        binding.setOrdfragment(viewmodel);
        binding.executePendingBindings();

        // setup RecycleView Fastfood
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(false);

        TicketAdapter adapter = new TicketAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(adapter);


        viewmodel = ViewModelProviders.of(this).get(TicketStoreVM.class);

        viewmodel.getLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setTicketAdapter(key);
            }
        });
        return binding.getRoot();
    }
}