package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemFilmsBinding;
import com.example.mvvm.model.Films;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.FilmsViewHolder> implements Filterable {
    List<Films> list,list_search;

    public FilmsAdapter(List<Films> list) {
        this.list = list;
        this.list_search = list;
    }

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilmsBinding binding = ItemFilmsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FilmsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmsViewHolder holder, int position) {
        Films films = list.get(position);
        if (films == null){return;}
        Picasso.get().load(films.poster).into(holder.binding.imageViewFilm);
        holder.binding.textViewnameFilm.setText(""+films.name);
        holder.binding.nameDirector.setText(films.director);
        holder.binding.textViewCMT.setText("217");
        holder.binding.textViewHeart.setText("748");

    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String keySearch = constraint.toString();
                if (keySearch.isEmpty()){
                    list = list_search;
                }else {
                    List<Films> temp = new ArrayList<>();
                    for (Films filmsSearch : list_search){
                        if (filmsSearch.getName().toLowerCase().contains(keySearch.toLowerCase())){
                            temp.add(filmsSearch);
                        }
                    }
                    list = temp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Films>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        private ItemFilmsBinding binding;
        public FilmsViewHolder(@NonNull ItemFilmsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
