package com.example.mvvm.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.LoginActivity;
import com.example.mvvm.R;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailUsersActivity;
import com.example.mvvm.function.FastfoodActivity;
import com.example.mvvm.function.FilmsActivity;
import com.example.mvvm.function.LiveTVActivity;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.function.UsersStatusActivity;
import com.example.mvvm.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SettingVM extends ViewModel {
    public ObservableField<Users> users = new ObservableField<>();

    public SettingVM() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(DataLocalManager.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.set(snapshot.getValue(Users.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onNextIntentFilm(View view){
        Intent intent = new Intent(view.getContext(), FilmsActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentShowTimes(View view){
        Intent intent = new Intent(view.getContext(), ShowTimesActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentUsersStatus(View view){
        Intent intent = new Intent(view.getContext(), UsersStatusActivity.class);
        view.getContext().startActivity(intent);
    }

    public void onNextIntentUsersInf(View view){
        Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentFood(View view){
        Intent intent = new Intent(view.getContext(), FastfoodActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentLiveTV(View view){
        Intent intent = new Intent(view.getContext(), LiveTVActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onLogOut(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        view.getContext().startActivity(intent);
    }

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl){
        if (imgUrl==null){
            imageView.setImageResource(R.drawable.round_person_pin_24);
        }
        else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}