package com.example.mvvm.function;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogUploadBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.datalocal.MyApplication;
import com.example.mvvm.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailUsersVM extends ViewModel {
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private MutableLiveData<AppCompatActivity> context = new MutableLiveData<>();

    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> fullname = new ObservableField<>();
    public ObservableField<String> avatar = new ObservableField<>();
    public ObservableField<String> birthday = new ObservableField<>();
    public ObservableField<String> phonenumber = new ObservableField<>();

    public ObservableField<Boolean> male = new ObservableField<>();

    public ObservableField<Users> users = new ObservableField<>();
    public DetailUsersVM(ActivityResultRegistry registry) {
        cropImageLauncher = registry.register("crop_image", new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                CropImage.ActivityResult cropResult = CropImage.getActivityResult(result.getData());
                if (cropResult.isSuccessful()) {
                    Uri croppedUri = cropResult.getUri();
                    Log.d("DetailUsersVM", "croppedUri: " + croppedUri);
                    upToFirebaseStorage(croppedUri);
                } else {
                    Exception error = cropResult.getError();
                    Log.d("DetailUsersVM", "cropError: " + error);
                }
            }
        });

        pickImageLauncher = registry.register("select_image", new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        startCropActivity(result);
                    }
                });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            email.set(firebaseUser.getEmail());
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(DataLocalManager.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey() != null)
                {
                    users.set(snapshot.getValue(Users.class));
                    fullname.set(users.get().fullname);
                    avatar.set(users.get().avatar);
                    male.set(users.get().male);
                    birthday.set(users.get().birthday);
                    phonenumber.set(users.get().phonenumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, Uri imgUrl) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        } else {
            Picasso.get().load(imgUrl).into(imageView);
        }
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
                            avatar.set(downloadUrl.toString());
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

    public void onclickMale() {
        male.set(true);
    }

    public void onclickFemale() {
        male.set(false);
    }
    public void updateUserAvatar(String string){
        avatar.set(string);

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("Users").child(DataLocalManager.getUid()).child("avatar").setValue(string, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                if (error == null) {
//                    Log.d(TAG, "Update Avatar : Complete");
//                } else {
//                    Log.d(TAG, "Update Avatar : Error - "+error);
//                }
//            }
//        });
    }

    public void onclickBirthday(View view) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                birthday.set(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, day);
        pickerDialog.show();
    }
    public void onclickRegister(View view) {
        String error = "";

        if (phonenumber == null) {
            error = "Bổ sung số điện thoại";
        } else if (phonenumber.get().length() != 10) {
            error = "Số điện thoại Không hợp lệ (!=10)";
        } else {
            Pattern pattern = Pattern.compile("0\\d{9}");
            Matcher matcher = pattern.matcher(phonenumber.get());
            int index = phonenumber.get().charAt(1);
            // "0" charAt = 48
            if (matcher.matches() && index != 48) {
            } else {
                error = "Số điện thoại không đúng định dạng";
            }
        }
        if (birthday.get() == null) {
            error = "Bổ sung ngày sinh";
        }
        if (male.get() == null) {
            error = "Bổ sung giới tính";
        }
        if (fullname.get() == null) {
            error = "Bổ sung Họ và Tên";
        } else if (fullname.get().length() < 5) {
            error = "Kiểm tra lại Họ và Tên";
        }
        if (avatar.get() == null) {
            error = "Bổ sung ảnh đại diện";
        }

        if (error.length() > 1) {
            Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
        } else {
            completeRegister(view);
        }
    }

    private void completeRegister(View view) {
        if (DataLocalManager.getAdmin())
        {
            users.set(new Users(avatar.get(),fullname.get(),male.get(),birthday.get(),email.get(),true,phonenumber.get(),true));
        }else {
            users.set(new Users(avatar.get(),fullname.get(),male.get(),birthday.get(),email.get(),false,phonenumber.get(),true));
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(DataLocalManager.getUid()).setValue(users.get(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}