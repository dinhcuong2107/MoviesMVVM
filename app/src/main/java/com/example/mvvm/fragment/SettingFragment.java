package com.example.mvvm.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivitySettingFragmentBinding;

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivitySettingFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_setting_fragment,container,false);
        binding.setSettingfragment(new SettingVM());

        return binding.getRoot();
    }
}