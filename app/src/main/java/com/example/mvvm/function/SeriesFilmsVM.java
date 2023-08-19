package com.example.mvvm.function;

import android.content.Intent;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.datalocal.DataLocalManager;

public class SeriesFilmsVM extends ViewModel {

    public ObservableField<Boolean> admin  = new ObservableField<>();

    public SeriesFilmsVM() {
        admin.set(DataLocalManager.getAdmin());
    }

    public void onNextIntentAddSeriesFilms(View view){
        Intent intent = new Intent(view.getContext(), AddSeriesFilmsActivity.class);
        view.getContext().startActivity(intent);
    }
}