package com.example.mvvm.fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.LoginActivity;
import com.example.mvvm.SplashScreenActivity;
import com.example.mvvm.datalocal.DataLocalManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieStoreVM extends ViewModel {
    private MutableLiveData<List<String>> uniqueListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> notInBListLiveData = new MutableLiveData<>();

    public ObservableField<Boolean> showSeries = new ObservableField<Boolean>();
    public ObservableField<Boolean> showText = new ObservableField<Boolean>();

    public MutableLiveData<List<String>> getUniqueListLiveData() {
        return uniqueListLiveData;
    }

    public MutableLiveData<List<String>> getNotInBListLiveData() {
        return notInBListLiveData;
    }

    public MovieStoreVM() {
        showSeries.set(true);
    }

    public void fetchDataFromFirebase() {
        DatabaseReference referenceMovieStore = FirebaseDatabase.getInstance().getReference("Movie Store").child(DataLocalManager.getUid());
        referenceMovieStore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tham chiếu "Movie Store" tồn tại, tiếp tục với logic của bạn
                    List<String> listMovieStore = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "Movie Store: " + dataSnapshot.getKey());
                        listMovieStore.add(dataSnapshot.getKey());
                    }
                    // ...
                    DatabaseReference referenceSeries = FirebaseDatabase.getInstance().getReference("Series Films");
                    referenceSeries.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Tham chiếu "Series Films" tồn tại, tiếp tục với logic của bạn
                                List<String> listSeries = new ArrayList<>();


                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.d(TAG, "Series Films: " + dataSnapshot.getKey());
                                    listSeries.add(dataSnapshot.getKey());
                                }
                                // ...

                                List<String> notInBList = new ArrayList<>(listMovieStore);
                                List<String> uniqueList  = new ArrayList<>();


                                // Kiểm tra xem cả hai danh sách đã được cập nhật từ Firebase chưa
                                if (!listMovieStore.isEmpty() && !listSeries.isEmpty()) {
                                    // Tạo danh sách uniqueList chứa các phần tử xuất hiện cả trong listMovieStore và listSeries

                                    for (String item : listSeries) {
                                        if (!listMovieStore.contains(item)) {
                                            uniqueList.add(item);
                                        }
                                    }
                                }
                                uniqueListLiveData.setValue(uniqueList);

                                notInBList.removeAll(uniqueListLiveData.getValue());
                                notInBListLiveData.setValue(notInBList);

                            } else {
                                // Tham chiếu "Series Films" không tồn tại
                                // Xử lý tình huống này theo ý muốn của bạn
                                // ...
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    // Tham chiếu "Movie Store" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void clickSetShowSeries(View view, Boolean show){

        showSeries.set(show);

        if (show){
            if (uniqueListLiveData.getValue().size()>0) {
                showText.set(false);
            } else {showText.set(true);}
        } else {
            if (notInBListLiveData.getValue().size()>0) {
                showText.set(false);
            } else {showText.set(true);}
        }
    }
}