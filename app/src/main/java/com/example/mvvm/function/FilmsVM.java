package com.example.mvvm.function;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogFilterFilmsBinding;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilmsVM extends ViewModel {
    public ObservableField<String> filmsCategory = new ObservableField<>();
    private MutableLiveData<List<String>> liveData;
    public FilmsVM() {
        if (filmsCategory.get() == null){
            filmsCategory.set("Tất cả");
        }
    }
    public MutableLiveData<List<String>> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<String>>();
            loadData();
        }
        return liveData;
    }
    public void setLiveData(MutableLiveData<List<String>> liveData) {
        this.liveData = liveData;
    }

    public void onSelectFilter(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogFilterFilmsBinding dialogBinding = CustomDialogFilterFilmsBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(dialogBinding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        dialogBinding.categoryall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsCategory.set(dialogBinding.categoryall.getText().toString());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsCategory.set(dialogBinding.categoryoff.getText().toString());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsCategory.set(dialogBinding.categoryon.getText().toString());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryhot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsCategory.set(dialogBinding.categoryhot.getText().toString());
                dialog.dismiss();
            }
        });
        dialogBinding.categorynew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filmsCategory.set(dialogBinding.categorynew.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void onNextIntentAddFilms(View view){
        Intent intent = new Intent(view.getContext(), AddFilmsActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onBack(View view){
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
    }
    private void loadData() {
        if (filmsCategory.equals("Phim online")){
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
        }else if (filmsCategory.equals("Phim offline")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Films films = dataSnapshot.getValue(Films.class);
                        if (films.status==false){
                            list.add(dataSnapshot.getKey());
                        }
                    }
                    liveData.setValue(list);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if (filmsCategory.equals("Phim mới")){
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
        }else if (filmsCategory.equals("Phim nổi bật")){
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
        }else {

        }
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
}