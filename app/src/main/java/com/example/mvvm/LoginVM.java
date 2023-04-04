package com.example.mvvm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginVM extends BaseObservable{
    String email,password;

    public LoginVM() {
    }

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

    public void onClickLogin(View view){
        if (email != null)
        {
            if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                if (password!=null && password.length()>=8){
                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.custom_dialog_loading);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if (task.isSuccessful())
                            {
                                DataLocalManager.setUid(auth.getUid());
                                checkInfUsers(view);
                            }else {
                                Dialog dialog = new Dialog(view.getContext());
                                dialog.setContentView(R.layout.custom_dialog_error);
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                Window window = dialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.show();

                            }
                        }
                    });
                }else {
                    Toast.makeText(view.getContext(), "Vui lòng bổ sung mật khẩu\nMật khẩu chứa ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();}
            }else {Toast.makeText(view.getContext(),"Email không hợp lệ",Toast.LENGTH_LONG).show();}
        } else {Toast.makeText(view.getContext(),"Nhập email và mật khẩu",Toast.LENGTH_LONG).show();}

    }

    private void checkInfUsers(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean s_inf = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String Uid = dataSnapshot.getKey();
                    if (Uid.equals(DataLocalManager.getUid())) {
                        s_inf=  true;
                    }
                }
                if (s_inf){
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
                    view.getContext().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onNextIntentRegister(View view){
        Intent intent = new Intent(view.getContext(),RegisterActivity.class);
        view.getContext().startActivity(intent);
    }
}
