package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityScanQrcodeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCodeActivity extends AppCompatActivity {
    ScanQRCodeVM viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScanQrcodeBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_scan_qrcode);
        viewModel = new ViewModelProvider(this).get(ScanQRCodeVM.class);
        binding.setScanqrcode(viewModel);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        final Activity activity = this;

        // Khởi tạo IntentIntegrator cho việc quét mã QR
        final IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Đặt mã QR vào khung quét");

        // Bắt đầu quét mã QR khi nhấn nút "Scan QR Code"
        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrator.initiateScan();
            }
        });
//        findViewById(R.id.scanButton).setOnClickListener(v -> integrator.initiateScan());
    }

    // Hàm này được gọi khi kết quả quét mã QR trả về
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // Nếu quét thành công
            if (result.getContents() == null) {
                Toast.makeText(this, "Bạn đã hủy quét mã QR", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Kết quả quét: " + result.getContents(), Toast.LENGTH_SHORT).show();
                viewModel.setCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
