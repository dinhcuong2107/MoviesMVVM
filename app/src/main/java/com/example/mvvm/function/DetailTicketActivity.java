package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityDetailTicketBinding;

public class DetailTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailTicketBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_ticket);
        binding.setDetailticket(new DetailTicketVM());
    }
}