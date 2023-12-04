package com.example.mvvm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

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

import java.util.ArrayList;
import java.util.List;

public class RegisterVM extends ViewModel {
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> passwordagain = new ObservableField<>();

    public void onclickRegister(View view){
        String error ="";
        if (TextUtils.isEmpty(email.get()))
        {
            error = "Vui lòng bổ sung địa chỉ email";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.get()).matches())
        {error = "Email không hợp lệ!";}

        if (TextUtils.isEmpty(email.get()))
        {
            error = "Bạn chưa thiết lập mật khẩu";
        }else if (password.get().length() <8)
        {
            error = "Mật khẩu chưa đủ tính bảo mật";
        }else if (!password.get().equals(passwordagain.get()))
        {
            error = "Mật khẩu không trùng khớp";
        }

        if (error.length()<1){
            regiter(view);
        }else Utils.showError(view.getContext(), error);
    }

    private void regiter(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.custom_dialog_loading);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.get(),password.get()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
                    view.getContext().startActivity(intent);
                    DataLocalManager.setUid(firebaseAuth.getUid());
                    Toast.makeText(view.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Utils.showError(view.getContext(), "Email đã được xử dụng");
                }
                dialog.dismiss();
            }
        });
    }

    public void onNextIntentLogin(View view){
        Intent intent = new Intent(view.getContext(),LoginActivity.class);
        view.getContext().startActivity(intent);
    }
}
