package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.databinding.ActivityFilmsBinding;
import com.example.mvvm.livedata.FilmsNewLiveData;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilmsActivity extends AppCompatActivity {
    FilmsAdapter adapter;
    ActivityFilmsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_films);
        binding.setListfilms(new FilmsVM());
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();

        // setup RecycleView
        LinearLayoutManager layoutManagerNew = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
        binding.recyclerFilm.setLayoutManager(layoutManagerNew);
        binding.recyclerFilm.setHasFixedSize(false);

        adapter = new FilmsAdapter(new ArrayList<String>());
        binding.recyclerFilm.setAdapter(adapter);

        FilmsVM liveData = ViewModelProviders.of(this).get(FilmsVM.class);

        liveData.getLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> key) {
                adapter.setFilmsAdapter(key);
            }
        });

        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
//    public void loadData (){
//        String filmsCategory = binding.buttonSelectFilter.getText().toString();
//        if (filmsCategory != null){
//            if (filmsCategory.equals("Phim chiếu rạp")){
//                // setup RecycleView Phim Offline
//                ArrayList<Films> list = new ArrayList<>();
//                LinearLayoutManager layoutManagerALL = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
//                binding.recyclerFilm.setLayoutManager(layoutManagerALL);
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        list.clear();
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            Films films = dataSnapshot.getValue(Films.class);
//                            if (films.status && films.filmsOn==false){
//                                films.inf_short = dataSnapshot.getKey();
//                                list.add(films);
//                            }
//                        }
//                        adapter = new FilmsAdapter(list);
//                        adapter.notifyDataSetChanged();
//                        binding.recyclerFilm.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//            else if (filmsCategory.equals("Phim online")){
//                // setup RecycleView Phim Online
//                ArrayList<Films> list = new ArrayList<>();
//                LinearLayoutManager layoutManagerALL = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
//                binding.recyclerFilm.setLayoutManager(layoutManagerALL);
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        list.clear();
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            Films films = dataSnapshot.getValue(Films.class);
//                            if (films.status && films.filmsOn){
//                                list.add(films);
//                                films.inf_short = dataSnapshot.getKey();
//                            }
//                        }
//                        adapter = new FilmsAdapter(list);
//                        adapter.notifyDataSetChanged();
//                        binding.recyclerFilm.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            } else if (filmsCategory.equals("Phim nổi bật")){
//                // setup RecycleView Phim Hot
//                ArrayList<Films> list = new ArrayList<>();
//                LinearLayoutManager layoutManagerALL = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
//                binding.recyclerFilm.setLayoutManager(layoutManagerALL);
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        list.clear();
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            Films films = dataSnapshot.getValue(Films.class);
//                            if (films.status && films.filmsHot){
//                                list.add(films);
//                                films.inf_short = dataSnapshot.getKey();
//                            }
//                        }
//                        adapter = new FilmsAdapter(list);
//                        adapter.notifyDataSetChanged();
//                        binding.recyclerFilm.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            } else if (filmsCategory.equals("Phim mới")){
//                // setup RecycleView Phim New
//                ArrayList<Films> list = new ArrayList<>();
//                LinearLayoutManager layoutManagerALL = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
//                binding.recyclerFilm.setLayoutManager(layoutManagerALL);
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        list.clear();
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            Films films = dataSnapshot.getValue(Films.class);
//                            if (films.status && films.filmsNew){
//                                list.add(films);
//                                films.inf_short = dataSnapshot.getKey();
//                            }
//                        }
//                        adapter = new FilmsAdapter(list);
//                        adapter.notifyDataSetChanged();
//                        binding.recyclerFilm.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            } else {
//                // setup RecycleView Phim
//                ArrayList<Films> list = new ArrayList<>();
//                LinearLayoutManager layoutManagerALL = new LinearLayoutManager(binding.recyclerFilm.getContext(), RecyclerView.VERTICAL,false);
//                binding.recyclerFilm.setLayoutManager(layoutManagerALL);
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        list.clear();
//                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            Films films = dataSnapshot.getValue(Films.class);
//                            if (films.status){
//                                list.add(films);
//                                films.inf_short = dataSnapshot.getKey();
//                            }
//                        }
//                        adapter = new FilmsAdapter(list);
//                        adapter.notifyDataSetChanged();
//                        binding.recyclerFilm.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        }
//    }
}