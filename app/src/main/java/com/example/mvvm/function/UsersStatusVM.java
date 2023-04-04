package com.example.mvvm.function;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersStatusVM extends ViewModel {
    private MutableLiveData<List<Users>> liveData;
    private List<Users> list;

    public UsersStatusVM() {
        liveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

        }
//        list.add();
        liveData.setValue(list);

    }

    public MutableLiveData<List<Users>> getLiveData() {
        return liveData;
    }
}
