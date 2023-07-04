package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityWalletBinding;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWalletBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);
        binding.setWallet(new WalletVM());
        binding.executePendingBindings();
    }
}