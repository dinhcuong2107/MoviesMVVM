package com.example.mvvm.function;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsAdapter;
import com.example.mvvm.databinding.CustomDialogFilterFilmsBinding;
import com.example.mvvm.databinding.CustomDialogGenreBinding;
import com.example.mvvm.livedata.FilmsOfflineVM;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilmsVM extends BaseObservable {
    String filmsCategory;

    public FilmsVM() {
    }

    @Bindable
    public String getFilmsCategory() {
        return filmsCategory;
    }

    public void setFilmsCategory(String filmsCategory) {
        this.filmsCategory = filmsCategory;
        notifyPropertyChanged(BR.filmsCategory);
    }

    public void onSelectFilter(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogFilterFilmsBinding dialogBinding = CustomDialogFilterFilmsBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(dialogBinding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        dialogBinding.categoryall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilmsCategory(""+ dialogBinding.categoryall.getText());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilmsCategory(""+ dialogBinding.categoryoff.getText());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilmsCategory(""+ dialogBinding.categoryon.getText());
                dialog.dismiss();
            }
        });
        dialogBinding.categoryhot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilmsCategory(""+ dialogBinding.categoryhot.getText());
                dialog.dismiss();
            }
        });
        dialogBinding.categorynew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilmsCategory(""+ dialogBinding.categorynew.getText());
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void onNextIntentAddFilms(View view){
        Intent intent = new Intent(view.getContext(), AddFilmsActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onBack(View view){
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
    }
}