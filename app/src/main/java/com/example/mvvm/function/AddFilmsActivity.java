package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityAddFilmsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hbb20.CountryCodePicker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class AddFilmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddFilmsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_films);
        AddFilmsVM addFilmsVM = new AddFilmsVM(getActivityResultRegistry());

        Intent intent = getIntent();
        String key_film = intent.getStringExtra("key_film");
        Log.e(TAG, "onCreate: "+ key_film );
        if (key_film != null){
            addFilmsVM.setIDFilm(key_film);
        }

        addFilmsVM.setCountry(binding.country.getSelectedCountryName());

        binding.setAddnewfilm(addFilmsVM);
        binding.executePendingBindings();
        binding.country.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                addFilmsVM.setCountry(binding.country.getSelectedCountryName());
            }
        });
    }
}