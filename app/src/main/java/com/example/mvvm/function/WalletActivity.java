package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.adapter.TransactionAdapter;
import com.example.mvvm.databinding.ActivityWalletBinding;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.livedata.TransactionMoneyLiveData;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWalletBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);
        binding.setWallet(new WalletVM());
        binding.executePendingBindings();

        // setup RecycleView
        LinearLayoutManager layoutManagerNew = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerview.setLayoutManager(layoutManagerNew);
        binding.recyclerview.setHasFixedSize(false);

        TransactionAdapter adapter = new TransactionAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(adapter);

        TransactionMoneyLiveData liveData = ViewModelProviders.of(this).get(TransactionMoneyLiveData.class);

        liveData.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setTransactionAdapter(key);
            }
        });
    }
}