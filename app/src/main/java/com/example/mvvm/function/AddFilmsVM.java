package com.example.mvvm.function;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.databinding.ActivityAddFilmsBinding;
import com.example.mvvm.databinding.CustomDialogGenreBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.databinding.CustomDialogSelectYearBinding;
import com.example.mvvm.model.Films;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.Resources;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddFilmsVM extends ViewModel {
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> pickVideoLauncher;

    public ObservableField<Boolean> update = new ObservableField<>();

    public ObservableField<String> key_film = new ObservableField<>();

    public ObservableField<String > type = new ObservableField<>();
    public ObservableField<String > films_name = new ObservableField<>();
    private MutableLiveData<AppCompatActivity> context = new MutableLiveData<>();
    public ObservableField<String > year_of_manufacture = new ObservableField<>();
    public ObservableField<String > genre = new ObservableField<>();
    public ObservableField<String > country = new ObservableField<>();
    public ObservableField<String > poster = new ObservableField<>();
    public ObservableField<String > video = new ObservableField<>();
    public ObservableField<String > trailer = new ObservableField<>();
    public ObservableField<String> director = new ObservableField<>();
    public ObservableField<String> main_actors = new ObservableField<>();
    public ObservableField<String> inf_short = new ObservableField<>();

    public AddFilmsVM(ActivityResultRegistry registry) {
        cropImageLauncher = registry.register("crop_image", new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                CropImage.ActivityResult cropResult = CropImage.getActivityResult(result.getData());
                if (cropResult.isSuccessful()) {
                    Uri croppedUri = cropResult.getUri();
                    Log.d("MainVM", "croppedUri: " + croppedUri);
                    upToFirebaseStorage(croppedUri);
                } else {
                    Exception error = cropResult.getError();
                    Log.d("MainVM", "cropError: " + error);
                }
            }
        });

        pickImageLauncher = registry.register("select_image", new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        startCropActivity(result);
                    }
                });
        pickVideoLauncher = registry.register("select_video", new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        upToFirebaseStorage(result);
                    }
                });
    }

    public void setCountry(String s){
        country.set(s);
    }

    public void setIDFilm(String s){
        key_film.set(s);
        if (key_film.get() == null){
            update.set(false);
        }else {
            update.set(true);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(key_film.get());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Films films = snapshot.getValue(Films.class);
                    films_name.set(films.name);
                    poster.set(films.poster);
                    year_of_manufacture.set(films.year);
                    genre.set(films.genre);
                    trailer.set(films.videoTrailer);
                    video.set(films.video);
                    director.set(films.director);
                    main_actors.set(films.main_actors);
                    inf_short.set(films.inf_short);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void onclickGenre(View view){
        final ArrayList<String> temp = new ArrayList<>();
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
                    genre.set(temp.toString());
                }
                dialog.dismiss();

            }
        });

        dialog.show();
    }
    public void onclickYear(View view){
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(Utils.getRealtime()));
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
                year_of_manufacture.set(""+newVal);
                binding.btnSeclect.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
    }
    public void onclickAddFilms(View view){
        if (onCheck(view))
        {
            Films films = new Films(films_name.get(), poster.get(), trailer.get(), video.get(), director.get(), main_actors.get(),country.get(), country.get(), year_of_manufacture.get(), inf_short.get(),true);

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
    public void onclickUpdateFilms(View view){
        if (onCheck(view) ) {
            Films films = new Films(films_name.get(), poster.get(), trailer.get(), video.get(), director.get(), main_actors.get(),country.get(), country.get(), year_of_manufacture.get(), inf_short.get(),true);
            
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Films").child(key_film.get()).setValue(films, new DatabaseReference.CompletionListener() {
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
    public void onclickDeleteFilms(View view){
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn chắc chắn muốn xóa phim");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());

        questionBinding.push.setText("Chắc chắn");
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Films").child(key_film.get()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(), "Xóa thành công",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Xóa không thành công",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        dialogquestion.show();

    }

    public boolean onCheck(View view){
        String error="";
        if (trailer.get() == null){error = "Bổ sung trailer phim";}
        Log.e(TAG, "onCheck: " +error);
        if (video.get() == null){error = "Bổ sung video phim";}
        Log.e(TAG, "onCheck: " +error);
        if (inf_short.get() == null){error = "Bổ sung tóm tắt phim";}
        Log.e(TAG, "onCheck: " +error);
        if (genre.get() == null){error = "Bổ sung thể loại phim";}
        Log.e(TAG, "onCheck: " +error);
        if (country.get() == null){error = "Bổ sung Quốc gia";}
        Log.e(TAG, "onCheck: " +error);
        if (main_actors.get() == null){error = "Bổ sung diễn viên chính";}
        Log.e(TAG, "onCheck: " +error);
        if (director.get() == null){error = "Bổ sung đạo diễn phim";}
        if (year_of_manufacture.get() == null){error = "Bổ sung năm sản xuất phim";}
        if (films_name.get() == null){error = "Bổ sung tên phim";}
        if (poster.get() == null){error = "Bổ sung poster phim";}

        if (error.length() >1 ){
            Toast.makeText(view.getContext(),""+error,Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCheck: " +error);
            return false;
        }else {
            return true;
        }
    }

    public void launchImagePicker(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        context.setValue(activity);
        type.set("image");

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickImageLauncher.launch("image/*");
                Log.e(TAG,"DetailUsersVM: Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(view.getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();
    }
    public void launchTrailerPicker(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        context.setValue(activity);
        type.set("trailer");
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickVideoLauncher.launch("video/*");
                Log.e(TAG,"DetailUsersVM: Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(view.getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();
    }
    public void launchVideoPicker(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        context.setValue(activity);
        type.set("video");

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickVideoLauncher.launch("video/*");
                Log.e(TAG,"DetailUsersVM: Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(view.getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();
    }
    private void startCropActivity(Uri uri) {
        AppCompatActivity activity = context.getValue();
        if (activity == null) {
            Log.e("DetailUsersVM", "Context is null");
            return;
        }
        Intent cropIntent = CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(27, 40)
                .getIntent(activity.getApplicationContext());

        cropImageLauncher.launch(cropIntent);
    }

    public void upToFirebaseStorage(Uri uri){
        // Up to FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        final StorageReference reference = storageReference.child("images/"+ UUID.randomUUID().toString());
        if (uri != null){

            Dialog dialog = new Dialog(context.getValue());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_upload);

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams win = window.getAttributes();
            win.gravity = Gravity.CENTER;
            window.setAttributes(win);
            dialog.setCancelable(false);

            TextView percentage = dialog.findViewById(R.id.textPercentage);
            ProgressBar progressBar = dialog.findViewById(R.id.progessPercentage);
            dialog.show();

            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            if (type.get().equals("image"))
                            {
                                poster.set(downloadUrl.toString());
                            } else if (type.get().equals("trailer"))
                            {
                                trailer.set(downloadUrl.toString());
                            } else if (type.get().equals("video"))
                            {
                                video.set(downloadUrl.toString());
                            }
                        }
                    });
                    Log.e(TAG,"message: Uploaded");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"message: Failed");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    int speed = (int) progress;
                    percentage.setText(speed + " %" );
                    progressBar.setProgress((int) speed);
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