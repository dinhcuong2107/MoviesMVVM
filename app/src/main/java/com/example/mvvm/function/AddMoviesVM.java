package com.example.mvvm.function;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import com.example.mvvm.MainVM;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogMoviesBinding;
import com.example.mvvm.datalocal.MyApplication;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class AddMoviesVM extends BaseObservable {
    String temporary;
    Movies movies;
    Films films;

    public AddMoviesVM() {
        String realtime = new SimpleDateFormat("dd/MM/yyyy").format(MainVM.Functions.getRealtime());
        movies = new Movies();
    }
    @Bindable
    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
        notifyPropertyChanged(BR.temporary);
    }

    @Bindable
    public Movies getMovies() {
        return movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
        notifyPropertyChanged(BR.movies);
    }
    @Bindable
    public Films getFilms() {
        return films;
    }

    public void setFilms(Films films) {
        this.films = films;
        notifyPropertyChanged(BR.films);
    }
    public void onClickMovies(View view){
        if (movies.key_film == null){ Toast.makeText(view.getContext(), "Bạn chưa chọn phim cần công chiếu", Toast.LENGTH_SHORT).show();}
        else if (movies.cinema == null){
            Toast.makeText(view.getContext(), "Bạn chưa chọn phòng chiếu", Toast.LENGTH_SHORT).show();}
        else if (movies.time == null){
            Toast.makeText(view.getContext(), "Bạn chưa chọn thời gian công chiếu", Toast.LENGTH_SHORT).show();}
        else if (movies.date == null){
            Toast.makeText(view.getContext(), "Bạn chưa chọn ngày công chiếu", Toast.LENGTH_SHORT).show();}
        else {
            dialogMovies(view);
        }

    }
    private void dialogMovies(View view) {
        final String[] keyMovies = new String[1];
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogMoviesBinding binding = CustomDialogMoviesBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        Picasso.get().load(films.poster).into(binding.filmsPosterNew);
        binding.filmsNameNew.setText(films.name);
        binding.filmsGenreNew.setText(films.genre);
        binding.filmsDirectorNew.setText(films.director);
        binding.filmsActorsNew.setText(films.main_actors);

        binding.cinema.setText(movies.cinema);
        binding.time.setText(movies.time + " - "+movies.date);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Movies");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keyMovies[0] = "";
                String keyFilms = "";
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Movies movies_temp = dataSnapshot.getValue(Movies.class);
                    if (movies_temp.cinema.equals(movies.cinema) && movies_temp.time.equals(movies.time) && movies_temp.date.equals(movies.date)){
                        keyMovies[0] = dataSnapshot.getKey();
                        keyFilms = movies_temp.key_film;
                    }
                }
                if (keyMovies[0].length() > 0){
                    binding.layout.setVisibility(View.VISIBLE);
                    binding.imagebutton.setVisibility(View.VISIBLE);
                    binding.push.setVisibility(View.GONE);
                    binding.replace.setVisibility(View.VISIBLE);
                    DatabaseReference databaseFilms = FirebaseDatabase.getInstance().getReference("Films").child(keyFilms);
                    databaseFilms.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Films temp = snapshot.getValue(Films.class);
                            Picasso.get().load(temp.poster).into(binding.filmsPoster);
                            binding.filmsName.setText(temp.name);
                            binding.filmsGenre.setText(temp.genre);
                            binding.filmsDirector.setText(temp.director);
                            binding.filmsActors.setText(temp.main_actors);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    binding.layout.setVisibility(View.GONE);
                    binding.imagebutton.setVisibility(View.GONE);
                    binding.push.setVisibility(View.VISIBLE);
                    binding.replace.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movies.status = true;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Movies").push().setValue(movies, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(v.getContext(),ShowTimesActivity.class);
                            v.getContext().startActivity(intent);
                        } else {
                            Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Movies").child(keyMovies[0]).child("key_film").setValue(movies.key_film);
                Intent intent = new Intent(v.getContext(),ShowTimesActivity.class);
                v.getContext().startActivity(intent);
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

    public void onClickCheck(){
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
                            setFilms(films);
                            movies.key_film = dataSnapshot.getKey();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.round_person_pin_24);
        } else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}