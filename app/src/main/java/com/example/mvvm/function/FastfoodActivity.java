package com.example.mvvm.function;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.adapter.FastfoodAdapter;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.databinding.ActivityFastfoodBinding;
import com.example.mvvm.databinding.CustomDialogNewFastfoodBinding;
import com.example.mvvm.livedata.FastfoodLiveData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FastfoodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFastfoodBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_fastfood);
        binding.setFastfood(new FastfoodVM(getActivityResultRegistry()));
        binding.executePendingBindings();

        // setup RecycleView Fastfood
        GridLayoutManager layoutManager = new GridLayoutManager(binding.recyclerview.getContext(), 3);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(false);

        FastfoodAdapter fastfoodAdapter = new FastfoodAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(fastfoodAdapter);

        FastfoodLiveData fastfoodLiveData = ViewModelProviders.of(this).get(FastfoodLiveData.class);
        fastfoodLiveData.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                fastfoodAdapter.setFastfoodAdapter(key);
            }
        });
    }
}