package com.example.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemPosterBinding;
import com.example.mvvm.databinding.ItemTopBinding;
import com.example.mvvm.function.DetailFilmsActivity;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmsTopAdapter extends RecyclerView.Adapter<FilmsTopAdapter.FilmsViewHolder>{
    List<String> list;

    public FilmsTopAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setFilmsTopAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmsTopAdapter.FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTopBinding binding = ItemTopBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FilmsTopAdapter.FilmsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmsTopAdapter.FilmsViewHolder holder, int position) {
        String key = list.get(position);
        if (key == null){return;}

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Films films = snapshot.getValue(Films.class);
                Picasso.get().load(films.poster).into(holder.binding.imageFilms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.imageFilms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailFilmsActivity.class);
                intent.putExtra("key_film", key);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        private ItemTopBinding binding;
        public FilmsViewHolder(@NonNull ItemTopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
