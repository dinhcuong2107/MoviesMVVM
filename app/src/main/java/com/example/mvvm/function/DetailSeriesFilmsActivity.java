package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.adapter.EpisodeAdapter;
import com.example.mvvm.databinding.ActivityDetailFilmsBinding;
import com.example.mvvm.databinding.ActivityDetailSeriesFilmsBinding;

import java.util.ArrayList;
import java.util.List;

public class DetailSeriesFilmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailSeriesFilmsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_series_films);

        DetailSeriesFilmsVM viewmodel =  ViewModelProviders.of(this).get(DetailSeriesFilmsVM.class);
        binding.setDetailSeriesFilms(viewmodel);
        binding.executePendingBindings();

        Intent intent = getIntent();
        String key_series = intent.getStringExtra("key_series");
        Log.e(TAG, "onCreate: "+ key_series );
        if (key_series != null){
            viewmodel.setKey_series(key_series);
        }

        // setup RecycleView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(false);

        EpisodeAdapter adapter = new EpisodeAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(adapter);

        viewmodel.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setEpisodeAdapter(key,false);
            }
        });

        adapter.setOnItemClickListener(new EpisodeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                viewmodel.load_epsode(position);
                binding.button.setText("Xem tập "+ (position + 1));
                Log.e(TAG, "onItemClick: item " + position );
            }
        });
    }
}