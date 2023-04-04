package com.example.mvvm.function;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityAddFilmsBinding;
import com.example.mvvm.model.Films;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class AddFilmsVM extends BaseObservable {
    int RESULT_LOAD_IMG = 0;
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
    public String getFilmsBanner () {
        return films.banner;
    }
    public void setFilmsBanner (String banner){
        films.setBanner(banner);
        notifyPropertyChanged(BR.filmsBanner);
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