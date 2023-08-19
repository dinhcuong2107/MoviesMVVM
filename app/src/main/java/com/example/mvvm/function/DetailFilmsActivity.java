package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FeedbackAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.databinding.ActivityDetailFilmsBinding;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailFilmsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailFilmsBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_films);
        Intent intent = getIntent();
        String key_film = intent.getStringExtra("key_film");
        binding.setDetailFilms(new DetailFilmsVM(key_film));
        binding.executePendingBindings();
    }
}