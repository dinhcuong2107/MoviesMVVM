package com.example.mvvm.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.library.baseAdapters.BR;

import com.example.mvvm.model.Films;

import java.util.ArrayList;
import java.util.List;

public class HomeVM extends BaseObservable {
    List<Films> getListFilms(){
        List<Films> list = new ArrayList<>();
        return list;
    }
}