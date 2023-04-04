package com.example.mvvm.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilmsVM extends BaseObservable {
    public Boolean isOn;
    public FilmsVM(Boolean isOn) {
        this.isOn = isOn;
    }
    @Bindable
    public Boolean getOn() {
        return isOn;
    }

    public void setOn(Boolean on) {
        isOn = on;
        notifyPropertyChanged(BR.on);
    }

    public void onclickfilmsoff(View view){
        setOn(false);
    }
    public void onclickfilmson(View view){
        setOn(true);
    }

    public void onNextIntentNew(View view){
        Intent intent = new Intent(view.getContext(), AddFilmsActivity.class);
        view.getContext().startActivity(intent);
    }
}}