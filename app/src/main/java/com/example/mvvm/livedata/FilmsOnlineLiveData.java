package com.example.mvvm.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilmsOnlineLiveData extends ViewModel {
    private MutableLiveData<List<String>> liveData;

    public MutableLiveData<List<String>> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<String>>();
            loadData();
        }
        return liveData;
    }

    private void loadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Films films = dataSnapshot.getValue(Films.class);
                    if (films.status){
                        list.add(dataSnapshot.getKey());
                    }
                }
                liveData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLiveData(MutableLiveData<List<String>> liveData) {
        this.liveData = liveData;
    }
}