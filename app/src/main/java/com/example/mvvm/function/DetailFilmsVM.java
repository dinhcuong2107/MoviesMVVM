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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FeedbackAdapter;
import com.example.mvvm.databinding.CustomDialogFeedbackBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailFilmsVM extends BaseObservable {
    String key_film,key_feedback,like,comment;
    Boolean feeling;
    Films films;
    public DetailFilmsVM(String key) {
        key_film = key;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(key_film);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setFilms(snapshot.getValue(Films.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        countcomment();
    }
    @Bindable
    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
        notifyPropertyChanged(BR.like);
    }
    @Bindable
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        notifyPropertyChanged(BR.comment);
    }
    @Bindable
    public Boolean getFeeling() {
        return feeling;
    }

    public void setFeeling(Boolean feeling) {
        this.feeling = feeling;
        notifyPropertyChanged(BR.feeling);
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
                    if (feedback.key_film.equals(key_film) && feedback.status){
                        if (feedback.comment.equals(":2107")) {
                            if (feedback.key_user.equals(DataLocalManager.getUid()))
                            {
                                key_feedback= dataSnapshot.getKey();
                                setFeeling(true);
                            }else setFeeling(false);
                            like++;}
                        else {comment++;}
                    }
                }
                setComment(""+comment);
                setLike(""+like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Bindable
    public Films getFilms() {
        return films;
    }

    public void setFilms(Films films) {
        this.films = films;
        notifyPropertyChanged(BR.films);
    }

    public void onclicktrailer(View view){
        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
        intent.putExtra("video", films.videoTrailer);
        intent.putExtra("name", films.name);
        view.getContext().startActivity(intent);
    }

    public void onclickvideo(View view){
        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
        intent.putExtra("video", films.video);
        intent.putExtra("name", films.name);
        view.getContext().startActivity(intent);
    }
    public void onclickbuy(View view){
        Intent intent = new Intent(view.getContext(), DetailTicketActivity.class);
        intent.putExtra("key_film", key_film);
        view.getContext().startActivity(intent);
    }

    public void onclicklike(View view){
        String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Feedback feedback = new Feedback();
        feedback.key_user = DataLocalManager.getUid();
        feedback.time = time;
        feedback.comment = ":2107";
        feedback.status = true;
        feedback.key_film = key_film;

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback").child(key_feedback);
        databaseReference.removeValue();
        setFeeling(false);
    }
    public void onclickfeedback(View view){
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
                    if (feedback.key_film.equals(key_film)){
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
                        feedback.key_film = key_film;

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