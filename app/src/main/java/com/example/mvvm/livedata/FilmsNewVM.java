package com.example.mvvm.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.model.Films;

import java.util.ArrayList;
import java.util.List;

public class FilmsNewVM extends ViewModel {

    private MutableLiveData<List<Films>> liveData;
    private List<Films> list;

    public FilmsNewVM() {
        liveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        list = new ArrayList<>();

        liveData.setValue(list);

    }

    public MutableLiveData<List<Films>> getLiveData() {
        return liveData;
    }
}