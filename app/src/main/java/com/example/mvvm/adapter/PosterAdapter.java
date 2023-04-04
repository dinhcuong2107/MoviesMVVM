package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemPosterBinding;
import com.example.mvvm.model.Films;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.FilmsViewHolder>{
    List<Films> list;

    public PosterAdapter(List<Films> list) {
        this.list = list;
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
