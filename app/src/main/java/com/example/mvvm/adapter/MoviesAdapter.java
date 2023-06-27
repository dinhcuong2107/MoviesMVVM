package com.example.mvvm.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Functions;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogMoviesBinding;
import com.example.mvvm.databinding.ItemMoviesBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailTicketActivity;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>{
    List<Movies> list;
    String keyfilmnew="";

    public MoviesAdapter(List<Movies> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoviesBinding binding = ItemMoviesBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MoviesAdapter.MoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        Movies movies = list.get(position);
        holder.binding.ciname.setText(movies.cinema);

        if (DataLocalManager.getAdmin()){
            holder.binding.edit.setVisibility(View.VISIBLE);
        }else {holder.binding.edit.setVisibility(View.GONE);}

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(movies.key_film);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Films films = snapshot.getValue(Films.class);
                holder.binding.films.setText(films.name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.films.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.binding.films.getText().length()>0)
                {
                    DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies");
                    databaseMovies.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                Movies movies1 = dataSnapshot.getValue(Movies.class);
                                if (movies1.key_film.equals(movies.key_film) && movies1.cinema.equals(movies.cinema) && movies1.time.equals(movies.time) && movies1.date.equals(movies.date))
                                {
                                    Intent intent = new Intent(v.getContext(), DetailTicketActivity.class);
                                    intent.putExtra("key_movies", dataSnapshot.getKey());
                                    v.getContext().startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Dialog dialog = new Dialog(v.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    CustomDialogMoviesBinding binding = CustomDialogMoviesBinding.inflate(LayoutInflater.from(v.getContext()));
                    dialog.setContentView(binding.getRoot());

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                    window.setGravity(Gravity.TOP);
                    dialog.setCancelable(false);

                    binding.cinema.setText(holder.binding.ciname.getText());
                    binding.time.setText(movies.time + " - "+movies.date);
                    binding.price.setText(""+movies.price);

                if (holder.binding.films.getText().length()==0) {
                    binding.layout.setVisibility(View.GONE);
                    binding.imagebutton.setVisibility(View.GONE);
                    binding.push.setVisibility(View.VISIBLE);
                    binding.replace.setVisibility(View.GONE);
                    binding.updateprice.setVisibility(View.GONE);
                }
                else {
                    binding.layout.setVisibility(View.VISIBLE);
                    binding.imagebutton.setVisibility(View.VISIBLE);
                    binding.push.setVisibility(View.GONE);
                    binding.replace.setVisibility(View.VISIBLE);
                    binding.updateprice.setVisibility(View.VISIBLE);

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Films").child(movies.key_film);
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Films films = snapshot.getValue(Films.class);
                            Picasso.get().load(films.poster).into(binding.filmsPoster);
                            binding.filmsName.setText(films.name);
                            binding.filmsGenre.setText(films.genre);
                            binding.filmsDirector.setText(films.director);
                            binding.filmsActors.setText(films.main_actors);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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
                ArrayAdapter<String> adapterfilms = new ArrayAdapter<>(v.getContext(), R.layout.item_selected,list);
                binding.autocompleteFilms.setAdapter(adapterfilms);
                binding.autocompleteFilms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String temporary = parent.getItemAtPosition(position).toString();
                        if (temporary.length()>0) {
                            String year = temporary.substring(temporary.length() - 4);
                            String name = temporary.substring(0, temporary.length() - 7);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        Films films = dataSnapshot.getValue(Films.class);
                                        if (films.name.equals(name) && films.year.equals(year)){
                                            keyfilmnew = dataSnapshot.getKey();
                                            Picasso.get().load(films.poster).into(binding.filmsPosterNew);
                                            binding.filmsGenreNew.setText(films.genre);
                                            binding.filmsDirectorNew.setText(films.director);
                                            binding.filmsActorsNew.setText(films.main_actors);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });

//                binding.price.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        String input = s.toString();
//                        if (!input.isEmpty()){
//                            binding.price.setText(Functions.convertPriceToVND(Integer.parseInt(input)));
//                            binding.price.addTextChangedListener(this);
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });
                binding.push.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (keyfilmnew.length()>0)
                        {
                            if (binding.price.getText().toString().length()>0){

                                movies.price = Integer.parseInt(binding.price.getText().toString().replace(".",""));
                                movies.status = true;
                                movies.key_film = keyfilmnew;
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Movies").push().setValue(movies, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(v.getContext(), ShowTimesActivity.class);
                                            v.getContext().startActivity(intent);
                                        } else {
                                            Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {Toast.makeText(v.getContext(), "Bổ sung giá vé", Toast.LENGTH_SHORT).show();}

                        } else {
                            Toast.makeText(v.getContext(), "Vui lòng chọn phim thay thế", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                binding.updateprice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.updateprice.getText().length()>0){
                            DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies");
                            databaseMovies.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        Movies movies1 = dataSnapshot.getValue(Movies.class);
                                        if (movies1.key_film.equals(movies.key_film) && movies1.cinema.equals(movies.cinema) && movies1.time.equals(movies.time) && movies1.date.equals(movies.date))
                                        {
                                            String price_new = binding.price.getText().toString().replaceAll(".","");
                                            DatabaseReference replaceprice = FirebaseDatabase.getInstance().getReference("Movies");
                                            replaceprice.child(dataSnapshot.getKey()).child("price").setValue(price_new);
                                            Intent intent = new Intent(v.getContext(),ShowTimesActivity.class);
                                            v.getContext().startActivity(intent);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else {Toast.makeText(v.getContext(), "Bổ sung giá vé", Toast.LENGTH_SHORT).show();}
                    }
                });
                binding.replace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (keyfilmnew.length()>0){
                            DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies");
                            databaseMovies.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        Movies movies1 = dataSnapshot.getValue(Movies.class);
                                        if (movies1.key_film.equals(movies.key_film) && movies1.cinema.equals(movies.cinema) && movies1.time.equals(movies.time) && movies1.date.equals(movies.date))
                                        {
                                            DatabaseReference replacefilm = FirebaseDatabase.getInstance().getReference("Movies");
                                            replacefilm.child(dataSnapshot.getKey()).child("key_film").setValue(keyfilmnew);
                                            Intent intent = new Intent(v.getContext(),ShowTimesActivity.class);
                                            v.getContext().startActivity(intent);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(v.getContext(), "Vui lòng chọn phim thay thế", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                binding.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder{
        private ItemMoviesBinding binding;

        public MoviesViewHolder(@NonNull ItemMoviesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
