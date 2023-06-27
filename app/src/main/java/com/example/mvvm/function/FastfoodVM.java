package com.example.mvvm.function;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.Functions;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogFastfoodBinding;
import com.example.mvvm.databinding.CustomDialogNewFastfoodBinding;
import com.example.mvvm.databinding.CustomDialogTicketVerificationBinding;
import com.example.mvvm.model.Fastfood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.UUID;

import kotlin.jvm.functions.Function1;

public class FastfoodVM extends ViewModel {
    CustomDialogNewFastfoodBinding bindingdialog;

    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private MutableLiveData<AppCompatActivity> context = new MutableLiveData<>();
    public ObservableField<String> image = new ObservableField<>();
    public FastfoodVM(ActivityResultRegistry registry) {
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
    }
    private void startCropActivity(Uri uri) {
        AppCompatActivity activity = context.getValue();
        if (activity == null) {
            Log.e("DetailUsersVM", "Context is null");
            return;
        }
        Intent cropIntent = CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .getIntent(activity.getApplicationContext());

        cropImageLauncher.launch(cropIntent);
    }
    public void openDialog(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingdialog = CustomDialogNewFastfoodBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(bindingdialog.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
        window.setGravity(Gravity.BOTTOM);

        if (image.get() != null){
            Picasso.get().load(image.get()).into(bindingdialog.image);
        }

        bindingdialog.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchImagePicker(v);
            }
        });

        bindingdialog.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bindingdialog.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bindingdialog.name.getText().length() == 0){
                    Toast.makeText(v.getContext(), "Thêm tên sản phẩm", Toast.LENGTH_SHORT).show();
                } else if (bindingdialog.price.getText().length() == 0){
                    Toast.makeText(v.getContext(), "Thêm giá sản phẩm", Toast.LENGTH_SHORT).show();
                }else if (image.get().length() == 0){
                    Toast.makeText(v.getContext(), "Thêm ảnh minh họa sản phẩm", Toast.LENGTH_SHORT).show();
                }else {
                    Fastfood fastfood = new Fastfood(bindingdialog.name.getText().toString(),image.get(),Integer.parseInt(bindingdialog.price.getText().toString()),true);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Fastfood").push().setValue(fastfood, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                Toast.makeText(v.getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(v.getContext(), FastfoodActivity.class);
                                v.getContext().startActivity(intent);
                            } else {
                                Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    public void launchImagePicker(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        context.setValue(activity);

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
                            image.set(downloadUrl.toString());
                            Picasso.get().load(image.get()).into(bindingdialog.image);
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
}