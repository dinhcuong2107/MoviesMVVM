package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityScanQrcodeBinding;

public class ScanQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScanQrcodeBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_scan_qrcode);
        binding.setScanqrcode(new ScanQRCodeVM());
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
    }
}