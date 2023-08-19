package com.example.mvvm.function;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class FilmsVM extends ViewModel {

    public ObservableField<Boolean> admin  = new ObservableField<>();

    public FilmsVM() {
        admin.set(DataLocalManager.getAdmin());
    }

    public void onNextIntentAddFilms(View view){
        Intent intent = new Intent(view.getContext(), AddFilmsActivity.class);
        view.getContext().startActivity(intent);
    }
}