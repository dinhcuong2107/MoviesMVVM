package com.example.mvvm.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm.R;
import com.example.mvvm.adapter.BannerAdapter;
import com.example.mvvm.databinding.ActivityHomeFragmentBinding;
import com.example.mvvm.effect.ZoomOutPageTransformer;
import com.example.mvvm.model.Films;

import java.util.List;

public class HomeFragment extends Fragment {
    private ActivityHomeFragmentBinding binding;
    HomeVM homeVM = new HomeVM();
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.viewpagerHomeFragment.getCurrentItem()== homeVM.getListFilms().size() - 1){
                binding.viewpagerHomeFragment.setCurrentItem(0);
            }else {
                binding.viewpagerHomeFragment.setCurrentItem(binding.viewpagerHomeFragment.getCurrentItem() + 1);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home_fragment,container,false);
        binding.setHomefragment(new HomeVM());

// setup ViewPager2
        BannerAdapter adapter = new BannerAdapter(homeVM.getListFilms());
        binding.viewpagerHomeFragment.setAdapter(adapter);
        binding.viewpagerHomeFragment.setPageTransformer(new ZoomOutPageTransformer());
        binding.viewpagerHomeFragment.setOffscreenPageLimit(3);
        binding.indicatorHomeFragment.setViewPager(binding.viewpagerHomeFragment);
        binding.viewpagerHomeFragment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable,5000);
            }
        });
// setup RecycleView Phim mới
//        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
//        binding.recyclerNewFilm.setLayoutManager(layoutManager);
//        FilmsNewVM filmsNewVM = new ViewModelProvider(this).get(FilmsNewVM.class);
//        filmsNewVM.getLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<Films>>() {
//            @Override
//            public void onChanged(List<Films> films) {
//                PosterFilmsAdapter adapter = new PosterFilmsAdapter(films);
//                binding.recyclerNewFilm.setAdapter(adapter);
//            }
//        });
// setup RecycleView Phim nổi bật
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
//        binding.recyclerHotFilm.setLayoutManager(layoutManager1);
//
//        FilmsHotVM filmsHotVM = new ViewModelProvider(this).get(FilmsHotVM.class);
//        filmsHotVM.getLiveData().observe(this.getViewLifecycleOwner(), new Observer<List<Films>>() {
//            @Override
//            public void onChanged(List<Films> films) {
//                PosterFilmsAdapter adapter = new PosterFilmsAdapter(films);
//                binding.recyclerHotFilm.setAdapter(adapter);
//            }
//        });
// setup RecycleView Combo khuyến mãi
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerDiscount.setLayoutManager(layoutManager2);
// setup RecycleView Vé Trò trơi
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerGame.setLayoutManager(layoutManager3);

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable,5000);
    }

}