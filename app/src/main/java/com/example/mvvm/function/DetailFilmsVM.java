package com.example.mvvm.function;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.adapter.FeedbackAdapter;
import com.example.mvvm.databinding.CustomDialogFeedbackBinding;
import com.example.mvvm.databinding.CustomDialogMoviesBinding;
import com.example.mvvm.databinding.CustomDialogOnlineFilmsBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.example.mvvm.model.OnlineFilms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;

public class DetailFilmsVM extends ViewModel {
    public ObservableField<String> key_film = new ObservableField<>();
    public ObservableField<String> key_feedback = new ObservableField<>();
    public ObservableField<String> quantityLike = new ObservableField<>();
    public ObservableField<String> quantityComment= new ObservableField<>();

    public ObservableField<Boolean> feeling = new ObservableField<>();

    public ObservableField<Films> films = new ObservableField<>();
    public ObservableField<String> onlinePrice = new ObservableField<>();
    public DetailFilmsVM(String key) {
        key_film.set(key);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(key_film.get());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                films.set(snapshot.getValue(Films.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        countcomment();
        checkOnline();
    }

    private void checkOnline() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Online Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    OnlineFilms onlineFilms = dataSnapshot.getValue(OnlineFilms.class);
                    if (onlineFilms.key_film.equals(key_film.get()))
                    {onlinePrice.set(Utils.convertPriceToVND(onlineFilms.price));}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countcomment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int like = 0;
                int comment = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback.key_film.equals(key_film.get()) && feedback.status){
                        if (feedback.comment.equals(":2107")) {
                            like++;
                            if (feedback.key_user.equals(DataLocalManager.getUid())) {
                                key_feedback.set(dataSnapshot.getKey());
                                feeling.set(true);
                            }
                        }
                        else {comment++;}
                    }
                }
                quantityComment.set(""+comment);
                quantityLike.set(""+like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onclicktrailer(View view){
        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
        intent.putExtra("video", films.get().videoTrailer);
        intent.putExtra("name", films.get().name);
        view.getContext().startActivity(intent);
    }

    public void onclickvideo(View view){
        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
        intent.putExtra("video", films.get().video);
        intent.putExtra("name", films.get().name);
        view.getContext().startActivity(intent);
    }
    public void onclickbuy(View view){
        Intent intent = new Intent(view.getContext(), DetailTicketActivity.class);
        intent.putExtra("key_film", key_film);
        view.getContext().startActivity(intent);
    }

    public void onShowPopupMenu(View view){
        Button button = view.findViewById(R.id.button);
        PopupMenu popupMenu = new PopupMenu(view.getContext(),button);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_films,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_online:
                        openDialogSetOnlineFilms(view);
                        return true;
                    case R.id.item_online_delete:
                        return true;
                    case R.id.item_hot:
                        return true;
                    case R.id.item_hot_delete:
                        return true;
                    case R.id.item_new:
                        return true;
                    case R.id.item_new_delete:
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openDialogSetOnlineFilms(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogOnlineFilmsBinding binding = CustomDialogOnlineFilmsBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        Picasso.get().load(films.get().poster).into(binding.filmsPoster);
        binding.filmsName.setText(films.get().name);
        binding.filmsGenre.setText(films.get().genre);
        binding.filmsDirector.setText(films.get().director);
        binding.filmsActors.setText(films.get().main_actors);
        binding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.price.getText().length()>0){
                    OnlineFilms onlineFilms = new OnlineFilms();
                    onlineFilms.key_film = key_film.get();
                    onlineFilms.price = Integer.valueOf(binding.price.getText().toString());

                    Dialog dialogquestion = new Dialog(view.getContext());
                    dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                    dialogquestion.setContentView(questionBinding.getRoot());

                    Window window = dialogquestion.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                    window.setGravity(Gravity.CENTER);
                    dialogquestion.setCancelable(false);

                    questionBinding.textview.setText("Bạn muốn mở chiếu phim: "+films.get().name +"\n với giá: " + Utils.convertPriceToVND(onlineFilms.price));
                    questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
                    questionBinding.push.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Online Films").push().setValue(onlineFilms, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        reloadIntent(v);
                                        Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    dialogquestion.show();
                }else {
                    Toast.makeText(v.getContext(), "Bổ sung giá", Toast.LENGTH_SHORT).show();
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

    private void reloadIntent(View v) {
        Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(v.getContext(), DetailFilmsActivity.class);
        intent.putExtra("key_film", key_film.get());
        v.getContext().startActivity(intent);
    }

    public void onclicklike(View view){
        String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Feedback feedback = new Feedback();
        feedback.key_user = DataLocalManager.getUid();
        feedback.time = time;
        feedback.comment = ":2107";
        feedback.status = true;
        feedback.key_film = key_film.get();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Feedback").push().setValue(feedback, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    countcomment();
                } else {Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();}
            }
        });
    }
    public void onclickdislike(View view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback").child(key_feedback.get());
        databaseReference.removeValue();
            }
    public void onclickfeedback(@NonNull View view){
        ArrayList<Feedback> list = new ArrayList<>();

        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogFeedbackBinding binding = CustomDialogFeedbackBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
        window.setGravity(Gravity.BOTTOM);
        // load recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(layoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback.key_film.equals(key_film.get())){
                        if (feedback.comment.equals(":2107"))
                        {

                        }else {
                            list.add(feedback);
                        }

                    }
                }
                FeedbackAdapter adapter = new FeedbackAdapter(list,view.getContext());
                binding.recyclerview.setHasFixedSize(true);
                binding.recyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.buttonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edittextsend.getText().toString().length()>0)
                    {
                        String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
                        Feedback feedback = new Feedback();
                        feedback.key_user = DataLocalManager.getUid();
                        feedback.time = time;
                        feedback.comment = binding.edittextsend.getText().toString();
                        feedback.status = true;
                        feedback.key_film = key_film.get();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Feedback").push().setValue(feedback, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    binding.edittextsend.setText("");
                                } else {Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();}
                            }
                        });
                    }
                        }
                    });
        dialog.show();
    }
    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl){
        if (imgUrl==null){
            imageView.setImageResource(R.drawable.ic_video_library_24);
        }
        else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}