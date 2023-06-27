package com.example.mvvm.function;

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
    int RESULT_LOAD_IMG = 0;
    int type;
    AddFilmsVM addFilmsVM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddFilmsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_films);
        addFilmsVM = new AddFilmsVM();

        addFilmsVM.setCountry(binding.country.getSelectedCountryName());
        binding.setAddnewfilm(addFilmsVM);
        binding.executePendingBindings();
        binding.country.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                addFilmsVM.setCountry(binding.country.getSelectedCountryName());
            }
        });
        binding.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        type = 0;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_LOAD_IMG);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(AddFilmsActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .check();
            }
        });
        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        type = 1;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        startActivityForResult(intent, RESULT_LOAD_IMG);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(AddFilmsActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .check();
            }
        });
        binding.trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        type = 2;
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        startActivityForResult(intent, RESULT_LOAD_IMG);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(AddFilmsActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\nVui lòng cấp quyền tại [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .check();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                final StorageReference reference = storageReference.child("images/"+ UUID.randomUUID().toString());
                if (imageUri != null){

                    Dialog dialog = new Dialog(AddFilmsActivity.this);
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

                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    switch (type){
                                        case 0: addFilmsVM.setPoster(downloadUrl.toString());break;
                                        case 1: addFilmsVM.setVideo(downloadUrl.toString());break;
                                        case 2: addFilmsVM.setTrailer(downloadUrl.toString());break;
                                    }
                                }
                            });

                            Toast.makeText(AddFilmsActivity.this,"Uploaded",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFilmsActivity.this,"Failed",Toast.LENGTH_LONG).show();
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
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                Toast.makeText(AddFilmsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(AddFilmsActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}