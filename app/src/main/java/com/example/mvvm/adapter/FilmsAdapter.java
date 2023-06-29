package com.example.mvvm.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemFilmsBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailFilmsActivity;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.FilmsViewHolder> implements Filterable {
    List<String> list,list_search;

    public FilmsAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }
    public void setFilmsAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilmsBinding binding = ItemFilmsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FilmsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmsViewHolder holder, int position) {
        String string = list.get(position);

        if (string == null){return;}
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(string);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Films films = snapshot.getValue(Films.class);
                Picasso.get().load(films.poster).into(holder.binding.imageViewFilm);
                holder.binding.textViewnameFilm.setText(films.name);
                holder.binding.nameDirector.setText("Đạo diễn: "+films.director);
                holder.binding.mainActor.setText("Diễn viên: "+films.main_actors);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailFilmsActivity.class);
                intent.putExtra("key_film", list.get(position));
                v.getContext().startActivity(intent);
            }
        });

        DatabaseReference databaseFeedback = FirebaseDatabase.getInstance().getReference("Feedback");
        databaseFeedback.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int like = 0;
                int comment = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback.key_film.equals(list.get(position)) && feedback.status){
                        if (feedback.comment.equals(":2107")) {
                            like++;}
                        else {comment++;}
                    }
                }
                holder.binding.textViewCMT.setText(""+comment);
                holder.binding.textViewHeart.setText(""+like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(filmsSearch);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Films films = snapshot.getValue(Films.class);
                                if (films.name.toLowerCase().contains(keySearch.toLowerCase())){
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
        private ItemFilmsBinding binding;
        public FilmsViewHolder(@NonNull ItemFilmsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
