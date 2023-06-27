package com.example.mvvm.fragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Fastfood;
import com.example.mvvm.model.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicketStoreVM extends ViewModel {
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ticket");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    if (DataLocalManager.getAdmin())
                    {
                        list.add(dataSnapshot.getKey());
                    }else {
                        if (ticket.key_user.equals(DataLocalManager.getUid())){
                            list.add(dataSnapshot.getKey());
                        }
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