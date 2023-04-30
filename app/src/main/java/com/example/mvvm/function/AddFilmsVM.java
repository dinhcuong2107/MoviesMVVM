package com.example.mvvm.function;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityAddFilmsBinding;
import com.example.mvvm.databinding.CustomDialogGenreBinding;
import com.example.mvvm.databinding.CustomDialogSelectYearBinding;
import com.example.mvvm.model.Films;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.Resources;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddFilmsVM extends BaseObservable {
    private Films films;
    @Bindable
    public Films getFilms() {
        return films;
    }

    public void setFilms(Films films) {
        this.films = films;
        notifyPropertyChanged(BR.films);
    }

    public AddFilmsVM() {
        films = new Films();
        films.filmsNew = true;
        films.filmsHot = false;
        films.filmsOn = false;
    }
    @Bindable
    public String getFilmsYear(){return films.year;}
    public void setFilmsYear(String year){
        films.setYear(year);
        notifyPropertyChanged(BR.filmsYear);
    }

    @Bindable
    public String getFilmsGenre(){return films.genre;}
    public void setFilmsGenre(String genre){
        films.setGenre(genre);
        notifyPropertyChanged(BR.filmsGenre);
    }

    @Bindable
    public String getFilmsCountry () {
        return films.country;
    }
    public void setFilmsCountry (String country){
        films.setCountry(country);
        notifyPropertyChanged(BR.filmsCountry);
    }


    @Bindable
    public String getFilmsPoster () {
        return films.poster;
    }
    public void setFilmsPoster (String poster){
        films.setPoster(poster);
        notifyPropertyChanged(BR.filmsPoster);
    }
    @Bindable
    public String getFilmsTrailer () {
        return films.videoTrailer;
    }
    public void setFilmsTrailer (String trailer){
        films.setVideoTrailer(trailer);
        notifyPropertyChanged(BR.filmsTrailer);
    }
    @Bindable
    public String getFilmsVideo () {
        return films.video;
    }
    public void setFilmsVideo (String video){
        films.setVideo(video);
        notifyPropertyChanged(BR.filmsVideo);
    }

    public void onclickOnline(){films.filmsOn = true;}
    public void onclickOffline(){films.filmsOn = false;}
    public void onclickHot(){films.filmsHot = true;films.filmsNew=false;}
    public void onclickNew(){films.filmsNew = true;films.filmsHot=false;}
    public void onclickOrther(){films.filmsNew = false;films.filmsHot=false;}

    public void onclickGenre(View view){
        final ArrayList<String> temp = new ArrayList<>();
        if (films.genre != null)
            {

            }
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogGenreBinding binding = CustomDialogGenreBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams win = window.getAttributes();
        win.gravity = Gravity.CENTER;
        window.setAttributes(win);
        dialog.setCancelable(false);

        binding.buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.checkboxGenre1.isChecked()){
                    temp.add(binding.checkboxGenre1.getText().toString());
                }
                if (binding.checkboxGenre2.isChecked()){
                    temp.add(binding.checkboxGenre2.getText().toString());
                }
                if (binding.checkboxGenre3.isChecked()){
                    temp.add(binding.checkboxGenre3.getText().toString());
                }
                if (binding.checkboxGenre4.isChecked()){
                    temp.add(binding.checkboxGenre4.getText().toString());
                }
                if (binding.checkboxGenre5.isChecked()){
                    temp.add(binding.checkboxGenre5.getText().toString());
                }
                if (binding.checkboxGenre6.isChecked()){
                    temp.add(binding.checkboxGenre6.getText().toString());
                }
                if (binding.checkboxGenre7.isChecked()){
                    temp.add(binding.checkboxGenre7.getText().toString());
                }
                if (binding.checkboxGenre8.isChecked()){
                    temp.add(binding.checkboxGenre8.getText().toString());
                }
                if (binding.checkboxGenre9.isChecked()){
                    temp.add(binding.checkboxGenre9.getText().toString());
                }

                if (temp.size()==0){
                    Toast.makeText(view.getContext(),"Phim chưa định dạng thể loại.",Toast.LENGTH_LONG).show();
                }else {
                    setFilmsGenre(temp.toString());
                }
                dialog.dismiss();

            }
        });

        dialog.show();
    }
    public void onclickYear(View view){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        CustomDialogSelectYearBinding binding = CustomDialogSelectYearBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams win = window.getAttributes();
        win.gravity = Gravity.CENTER;
        window.setAttributes(win);
        dialog.setCancelable(false);

        binding.yearpicker.setMinValue(1880);
        binding.yearpicker.setMaxValue(year);
        binding.yearpicker.setValue(year);

        binding.btnSeclect.setVisibility(View.GONE);
        binding.btnSeclect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.yearpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setFilmsYear(""+newVal);
                binding.btnSeclect.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
    }

    public void onclickCountry(View view){}
    public void onclickAddFilms(View view){
        String error="";
        if (films.videoTrailer == null){error = "Bổ sung trailer phim";}
        if (films.video == null){error = "Bổ sung video phim";}
        if (films.inf_short == null){error = "Bổ sung tóm tắt phim";}
        if (films.genre == null){error = "Bổ sung thể loại phim";}
        if (films.country == null){error = "Bổ sung Quốc gia";}
        if (films.main_actors == null){error = "Bổ sung diễn viên chính";}
        if (films.director == null){error = "Bổ sung đạo diễn phim";}
        if (films.year == null){error = "Bổ sung năm sản xuất phim";}
        if (films.name == null){error = "Bổ sung tên phim";}
        if (films.poster == null){error = "Bổ sung poster phim";}

        if (error.length() >1 ){
            Toast.makeText(view.getContext(),""+error,Toast.LENGTH_LONG).show();
        }else {
            films.status = true;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Films").push().setValue(films, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(view.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl){
        if (imgUrl==null){
            imageView.setImageResource(R.drawable.ic_video_library_24);
        }
        else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}