package com.example.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemPosterBinding;
import com.example.mvvm.datalocal.MyApplication;
import com.example.mvvm.function.DetailFilmsActivity;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.FilmsViewHolder>{
    List<Films> list;

    public PosterAdapter(List<Films> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPosterBinding binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FilmsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmsViewHolder holder, int position) {
        Films films = list.get(position);
        if (films == null){return;}
        Picasso.get().load(films.poster).into(holder.binding.imageViewItemFilm);
        holder.binding.textnameItemFilm.setText(""+films.name);
        holder.binding.imageViewItemFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Films data = dataSnapshot.getValue(Films.class);
                            if (data.name.equals(films.name) && data.video.equals(films.video)){
                                Intent intent = new Intent(MyApplication.getInstance(), DetailFilmsActivity.class);
                                intent.putExtra("key_film", dataSnapshot.getKey());
                                MyApplication.getInstance().startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        private ItemPosterBinding binding;
        public FilmsViewHolder(@NonNull ItemPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
