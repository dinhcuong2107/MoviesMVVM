package com.example.mvvm.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilmsAnimeVM extends ViewModel {

    private MutableLiveData<List<Films>> liveData;
    private List<Films> list;

    public FilmsAnimeVM() {
        liveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Films films = dataSnapshot.getValue(Films.class);
                    if (films.status){
                        if (films.genre.contains("Phim hoạt hình"))
                        {
                            list.add(films);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        liveData.setValue(list);

    }

    public MutableLiveData<List<Films>> getLiveData() {
        return liveData;
    }
}