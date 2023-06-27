package com.example.mvvm.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogMoviesBinding;
import com.example.mvvm.databinding.ItemShowtimesBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.datalocal.MyApplication;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowTimesAdapter extends RecyclerView.Adapter<ShowTimesAdapter.ShowTimesViewHolder> {
    List<String> time;
    String date;
    String key_movies_default;
    String key_films_default;
    String key_films_new;

    public ShowTimesAdapter(List<String> time,String date) {
        this.time = time;
        this.date = date;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowTimesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShowtimesBinding binding = ItemShowtimesBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ShowTimesAdapter.ShowTimesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowTimesViewHolder holder, int position) {
        holder.binding.category.setText(time.get(position));

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getInstance(),RecyclerView.VERTICAL,false);
        holder.binding.recyclerview.setLayoutManager(layoutManager);

        List<Movies> moviesList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moviesList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Movies movies = dataSnapshot.getValue(Movies.class);
                    if (movies.status && movies.time.equals(time.get(position)) && movies.date.equals(date)){
                        moviesList.add(movies);
                    }
                }
                switch (moviesList.size()){
                    case 0:
                        moviesList.add(new Movies("",time.get(position),date,"Cinema 1",0,true));
                        moviesList.add(new Movies("",time.get(position),date,"Cinema 2",0,true));
                        moviesList.add(new Movies("",time.get(position),date,"Cinema 3",0,true));
                        break;
                    case 1: int c = Integer.parseInt(String.valueOf(moviesList.get(0).cinema.charAt(moviesList.get(0).cinema.length() -1)));
                        if (c==1){
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 2",0,true));
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 3",0,true));
                        } else if (c==2){
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 1",0,true));
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 3",0,true));
                        }else if (c==3){
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 2",0,true));
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 1",0,true));
                        }
                        break;
                    case 2:
                        int a = Integer.parseInt(String.valueOf(moviesList.get(0).cinema.charAt(moviesList.get(0).cinema.length() -1)));
                        int b = Integer.parseInt(String.valueOf(moviesList.get(1).cinema.charAt(moviesList.get(1).cinema.length() -1)));
                        if (a*b == 2)
                        {
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 3",0,true));
                        }else if (a*b == 3)
                        {
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 2",0,true));
                        }else if (a*b == 6)
                        {
                            moviesList.add(new Movies("",time.get(position),date,"Cinema 1",0,true));
                        }
                            break;
                }
                Collections.sort(moviesList,(o1, o2) -> o1.cinema.compareTo(o2.cinema));
                MoviesAdapter adapter = new MoviesAdapter(moviesList);
                holder.binding.recyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (time != null){return time.size();}
        return 0;
    }

    public class ShowTimesViewHolder extends RecyclerView.ViewHolder{
        private ItemShowtimesBinding binding;

        public ShowTimesViewHolder(@NonNull ItemShowtimesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
