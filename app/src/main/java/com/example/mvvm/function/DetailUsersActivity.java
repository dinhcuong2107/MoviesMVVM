package com.example.mvvm.function;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityDetailUsersBinding;

public class DetailUsersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailUsersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_users);
        DetailUsersVM detailUsersVM = new DetailUsersVM(getActivityResultRegistry());
        binding.setDetailusers(detailUsersVM);
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();
    }
}