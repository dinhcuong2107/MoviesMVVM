package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mvvm.LoginActivity;
import com.example.mvvm.R;
import com.example.mvvm.SplashScreenActivity;
import com.example.mvvm.adapter.EpisodeAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.databinding.ActivityAddSeriesFilmsBinding;
import com.example.mvvm.livedata.FilmsAllLiveData;
import com.example.mvvm.livedata.FilmsHotLiveData;
import com.example.mvvm.livedata.FilmsOnlineLiveData;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddSeriesFilmsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddSeriesFilmsBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_add_series_films);
        AddSeriesFilmsVM viewmodel =  ViewModelProviders.of(this).get(AddSeriesFilmsVM.class);
        binding.setSeriesFilms(viewmodel);
        binding.executePendingBindings();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },0);

        FilmsOnlineLiveData liveData =  ViewModelProviders.of(this).get(FilmsOnlineLiveData.class);
        liveData.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list_online) {
                if (list_online.size() != 0) {

                    ArrayList<String> list = new ArrayList<>();

                    for (int i = 0; i < list_online.size(); i++)
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Films").child(list_online.get(i));
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Films films = snapshot.getValue(Films.class);
                                if (films.status){
                                    String name = films.name +" - "+films.year;
                                    list.add(name);
                                    Log.e(TAG, "Livedata Films On: " + list);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Livedata Films On: " + error);
                            }
                        });
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapterfilms = new ArrayAdapter<>(getApplicationContext(), R.layout.item_selected,list);
                            binding.autoComplete.setAdapter(adapterfilms);
                        }
                    },100);


                }
            }
        });

        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temporary = parent.getItemAtPosition(position).toString();
                if (temporary.length()>0) {
                    String year = temporary.substring(temporary.length() - 4);
                    String name = temporary.substring(0, temporary.length() - 7);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                Films films = dataSnapshot.getValue(Films.class);
                                if (films.name.equals(name) && films.year.equals(year)){

                                    viewmodel.setSelect(dataSnapshot.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        // setup RecycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.recyclerview.setHasFixedSize(false);

        EpisodeAdapter adapter = new EpisodeAdapter(new ArrayList<String>());
        binding.recyclerview.setAdapter(adapter);

        viewmodel.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setEpisodeAdapter(key,true);
                // Xóa phim đã chọn sau khi add
                binding.autoComplete.setText("");
            }
        });

        adapter.setOnItemClickListener(new EpisodeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e(TAG, "onItemClick: item " + position );
                viewmodel.delete_epsode(position);
            }
        });

        Intent intent = getIntent();
        String key_series = intent.getStringExtra("key_series");
        Log.e(TAG, "onCreate: "+ key_series );
        if (key_series != null){
            viewmodel.setSeries(key_series);
        }
    }
}