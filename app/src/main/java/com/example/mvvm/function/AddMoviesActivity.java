package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.mvvm.MainVM;
import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityAddMoviesBinding;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddMoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddMoviesBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_add_movies);
        binding.setAddmovies(new AddMoviesVM());
        binding.executePendingBindings();

        ArrayList<String> list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Films films = dataSnapshot.getValue(Films.class);
                    if (films.status){
                        String name = films.name +" - "+films.year;
                        list.add(name);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayAdapter<String> adapterfilms = new ArrayAdapter<>(this,R.layout.item_selected,list);
        binding.autocompleteFilms.setAdapter(adapterfilms);

        String[] cinema = new String[] {"Cinema 1", "Cinema 2", "Cinema 3"};
        ArrayAdapter<String> adaptercinema = new ArrayAdapter<>(this,R.layout.item_selected,cinema);
        binding.autocompleteCinema.setAdapter(adaptercinema);

        String[] time = getResources().getStringArray(R.array.timemovies);
        ArrayAdapter<String> adaptertime = new ArrayAdapter<>(this,R.layout.item_selected,time);
        binding.autocompleteTime.setAdapter(adaptertime);

        ArrayList<String> date = new ArrayList<>();
        for (int i=0; i<7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(MainVM.Functions.getRealtime());
            calendar.add(Calendar.DAY_OF_YEAR, i);
            String str = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            date.add(str);
        }
        ArrayAdapter<String> adapterdate = new ArrayAdapter<>(this,R.layout.item_selected,date);
        binding.autocompleteDate.setAdapter(adapterdate);

    }
}