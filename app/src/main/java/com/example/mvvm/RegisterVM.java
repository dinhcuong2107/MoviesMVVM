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
import androidx.databinding.library.baseAdapters.BR;

import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailUsersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterVM extends BaseObservable{
    public String email,password,passwordagain;
    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
    @Bindable
    public String getPasswordagain() {
        return passwordagain;
    }

    public void setPasswordagain(String passwordagain) {
        this.passwordagain = passwordagain;
        notifyPropertyChanged(BR.passwordagain);
    }

    public void onclickRegister(View view){
        String error ="";
        if (email == null)        {error = "Vui lòng bổ sung địa chỉ email";}
        else  if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){}
        else {error = "Email không hợp lệ!";}

        if (password == null)        {            error = "Bạn chưa thiết lập mật khẩu";
        }else if (password.length() <8){            error = "Mật khẩu chưa đủ tính bảo mật";
        }else if (password.equals(passwordagain)){}else {            error = "Mật khẩu không trùng khớp";}

        if (error.length()<1){
            regiter(view);
        }else Toast.makeText(view.getContext(), ""+error, Toast.LENGTH_SHORT).show();
    }

    private void regiter(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.custom_dialog_loading);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
                    view.getContext().startActivity(intent);
                    DataLocalManager.setUid(firebaseAuth.getUid());
                    Toast.makeText(view.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Thử lại", Toast.LENGTH_SHORT).show();
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
