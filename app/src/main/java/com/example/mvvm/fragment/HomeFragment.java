package com.example.mvvm.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mvvm.R;
import com.example.mvvm.adapter.FilmsTopAdapter;
import com.example.mvvm.adapter.PosterAdapter;
import com.example.mvvm.databinding.ActivityHomeFragmentBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.livedata.FilmsAnimeVM;
import com.example.mvvm.livedata.FilmsHotVM;
import com.example.mvvm.livedata.FilmsNewVM;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ActivityHomeFragmentBinding binding;
    List<Films> listTop = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home_fragment,container,false);
        binding.setHomefragment(new HomeVM());
        binding.setLifecycleOwner(this);
        binding.executePendingBindings();
        if (!DataLocalManager.getNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
// setup Viewpager
        setViewpager();
// setup ImageSlider
        ArrayList<SlideModel> list = new ArrayList<>();
        list.add(new SlideModel("https://kenh14cdn.com/203336854389633024/2023/3/30/photo-1-1680175123613568299569.jpg",ScaleTypes.FIT));
        list.add(new SlideModel("https://znews-photo.zingcdn.me/w660/Uploaded/kbd_pilk/2022_06_19/cuc3.jpg",ScaleTypes.FIT));
        list.add(new SlideModel("https://ss-images.saostar.vn/wp700/pc/1674912272908/saostar-pckqlefjvl3881vb.png",ScaleTypes.FIT));
        list.add(new SlideModel("https://kenh14cdn.com/203336854389633024/2022/8/4/ai-hinh-khong-he-gia-tran-cua-my-nhan-4000-nam-mat-thon-vai-gay-chan-dai-deu-la-don-1-16044151377011682443371-1607529849-689-width660height1004-1659598005458348527112.jpg",ScaleTypes.FIT));

// setup RecycleView
        setNewFilms();
        setHotFilms();
        setAnimeFilms();

// setup RecycleView Combo khuyến mãi
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerDiscount.setLayoutManager(layoutManager2);
// setup RecycleView Vé Trò trơi
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerGame.setLayoutManager(layoutManager3);

        return binding.getRoot();
    }

    private void setAnimeFilms() {
        LinearLayoutManager layoutManagerAnime = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerAnimeFilm.setLayoutManager(layoutManagerAnime);
        FilmsAnimeVM filmsAnimeVM = new ViewModelProvider(this).get(FilmsAnimeVM.class);
        filmsAnimeVM.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                PosterAdapter adapter = new PosterAdapter(films);
                binding.recyclerAnimeFilm.setAdapter(adapter);
            }

        });
    }

    private void setHotFilms() {
        LinearLayoutManager layoutManagerHot = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerHotFilm.setLayoutManager(layoutManagerHot);
        FilmsHotVM filmsHotVM = new ViewModelProvider(this).get(FilmsHotVM.class);
        filmsHotVM.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                PosterAdapter adapter = new PosterAdapter(films);
                binding.recyclerHotFilm.setAdapter(adapter);
            }
        });
    }

    private void setNewFilms() {
        ArrayList<Films> newfilms = new ArrayList<>();
        LinearLayoutManager layoutManagerNew = new LinearLayoutManager(binding.recyclerNewFilm.getContext(), RecyclerView.HORIZONTAL,false);
        binding.recyclerNewFilm.setLayoutManager(layoutManagerNew);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newfilms.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Films films = dataSnapshot.getValue(Films.class);
                    if (films.status){
                        newfilms.add(films);
                    }
                }
                PosterAdapter adapter = new PosterAdapter(newfilms);
                adapter.notifyDataSetChanged();
                binding.recyclerNewFilm.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        FilmsNewVM filmsNewVM = new ViewModelProvider(this).get(FilmsNewVM.class);
//        filmsNewVM.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<Films>>() {
//            @Override
//            public void onChanged(List<Films> films) {
//                PosterAdapter adapter = new PosterAdapter(films);
//                binding.recyclerNewFilm.setAdapter(adapter);
//            }
//        });
    }

    private void setViewpager() {
        binding.viewpager.setOffscreenPageLimit(3);
        binding.viewpager.setClipChildren(false);
        binding.viewpager.setClipToPadding(false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        binding.viewpager.setPageTransformer(compositePageTransformer);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTop.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Films films = dataSnapshot.getValue(Films.class);
                    if (films.status){
                        listTop.add(films);
                    }
                }
                FilmsTopAdapter adapter = new FilmsTopAdapter(listTop,getContext());
                binding.viewpager.setAdapter(adapter);
                binding.circleindicator.setViewPager(binding.viewpager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable,3000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable,3000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.viewpager.getCurrentItem()==listTop.size()-1){
                binding.viewpager.setCurrentItem(0);
            }else {
                binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() +1);
            }
        }
    };
}