package com.example.mvvm.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mvvm.fragment.HomeFragment;
import com.example.mvvm.fragment.MovieStoreFragment;
import com.example.mvvm.fragment.TicketStoreFragment;
import com.example.mvvm.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new HomeFragment();
            case 1: return new TicketStoreFragment();
            case 2: return new MovieStoreFragment();
            case 3: return new SettingFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
