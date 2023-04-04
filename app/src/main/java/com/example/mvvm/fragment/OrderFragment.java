package com.example.mvvm.fragment;

import static androidx.databinding.DataBindingUtil.setContentView;

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
import com.example.mvvm.databinding.ActivityOrderFragmentBinding;

public class OrderFragment extends Fragment {
    ActivityOrderFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_order_fragment,container,false);
        binding.setOrdfragment(new OrderVM());
        return binding.getRoot();
    }
}