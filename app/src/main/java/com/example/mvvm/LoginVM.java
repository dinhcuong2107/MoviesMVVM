package com.example.mvvm;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.databinding.CustomDialogChangePasswordBinding;
import com.example.mvvm.databinding.CustomDialogResetPasswordBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailUsersActivity;
import com.example.mvvm.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LoginVM extends ViewModel {
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();

    public void onClickResetPassword(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogResetPasswordBinding binding = CustomDialogResetPasswordBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        binding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setVisibility(View.VISIBLE);
                String userEmail = binding.textInputEmail.getText().toString();
                if (!TextUtils.isEmpty(userEmail) && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Yêu cầu khôi phục mật khẩu đã được gửi thành công
                                        Log.d(TAG, "Yêu cầu khôi phục mật khẩu đã được gửi thành công");
                                        binding.textview.setVisibility(View.VISIBLE);
                                        binding.progress.setVisibility(View.GONE);
                                        binding.push.setVisibility(View.GONE);
                                    } else {
                                        // Xảy ra lỗi khi gửi yêu cầu khôi phục mật khẩu
                                        Log.w(TAG, "Lỗi khi gửi yêu cầu khôi phục mật khẩu", task.getException());
                                        Toast.makeText(view.getContext(), "Lỗi khi gửi yêu cầu khôi phục mật khẩu", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    binding.textInputEmail.setError("email không hợp lệ");
                    binding.progress.setVisibility(View.GONE);
                }
            }
        });

        dialog.show();

    }

    public void onClickLogin(View view) {
        if (email != null) {
            if (!TextUtils.isEmpty(email.get()) && Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
                if (password != null && password.get().length() > 0) {
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.custom_dialog_loading);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email.get(), password.get()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                DataLocalManager.setUid(auth.getUid());
                                checkInfUsers(view);
                            } else {
                                Dialog dialog = new Dialog(view.getContext());
                                dialog.setContentView(R.layout.custom_dialog_error);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                Window window = dialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(view.getContext(), "Vui lòng bổ sung mật khẩu\nMật khẩu chứa ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Email không hợp lệ", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(), "Nhập email và mật khẩu", Toast.LENGTH_LONG).show();
        }

    }

    private void checkInfUsers(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(DataLocalManager.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null) {
                    if (users.status) {
                        DataLocalManager.setAdmin(users.admin);
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "Tài khoản đã bị khóa", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
                    view.getContext().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onNextIntentRegister(View view) {
        Intent intent = new Intent(view.getContext(), RegisterActivity.class);
        view.getContext().startActivity(intent);
    }
}
