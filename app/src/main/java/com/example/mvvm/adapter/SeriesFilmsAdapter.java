package com.example.mvvm.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Utils;
import com.example.mvvm.databinding.ItemPosterBinding;
import com.example.mvvm.function.DetailFilmsActivity;
import com.example.mvvm.function.DetailSeriesFilmsActivity;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.SeriesFilms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SeriesFilmsAdapter extends RecyclerView.Adapter<SeriesFilmsAdapter.FilmsViewHolder> implements Filterable {
    List<String> list, list_search;

    public SeriesFilmsAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }
    public void setAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
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
        // cho phép di chuyển nội dung hiển thị trên TextView
        holder.binding.textnameItemFilm.setSelected(true);

        String key = list.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Series Films").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SeriesFilms seriesFilms = snapshot.getValue(SeriesFilms.class);
                holder.binding.textnameItemFilm.setText(""+seriesFilms.name);

                List<String> temp = Utils.convertStringToList(seriesFilms.epsodecode);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Films").child(temp.get(0));
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Films films = snapshot.getValue(Films.class);
                        Picasso.get().load(films.poster).into(holder.binding.imageViewItemFilm);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.binding.imageViewItemFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailSeriesFilmsActivity.class);
                intent.putExtra("key_series", key);
                v.getContext().startActivity(intent);
            }
        });
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
                    List<String> temp = new ArrayList<>();
                    for (String filmsSearch : list_search){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Series Films").child(filmsSearch);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                SeriesFilms seriesFilms = snapshot.getValue(SeriesFilms.class);
                                if (seriesFilms.name.toLowerCase().contains(keySearch.toLowerCase())){
                                    temp.add(filmsSearch);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    list = temp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        private ItemPosterBinding binding;
        public FilmsViewHolder(@NonNull ItemPosterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
