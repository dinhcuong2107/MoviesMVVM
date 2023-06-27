package com.example.mvvm.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.model.Fastfood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FastfoodLiveData extends ViewModel {
    private MutableLiveData<List<String>> liveData;

    public MutableLiveData<List<String>> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<String>>();
            loadData();
        }
        return liveData;
    }

    private void loadData() {
        // Lấy dữ liệu từ database hoặc API
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fastfood");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Fastfood temp = dataSnapshot.getValue(Fastfood.class);
                    if (temp.status){
                        list.add(dataSnapshot.getKey());
                    }
                }
                // Trả về dữ liệu
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