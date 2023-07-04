package com.example.mvvm.fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.LoginActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.databinding.CustomDialogChangePasswordBinding;
import com.example.mvvm.databinding.CustomDialogDetailUsersBinding;
import com.example.mvvm.databinding.CustomDialogNewFastfoodBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailUsersActivity;
import com.example.mvvm.function.FastfoodActivity;
import com.example.mvvm.function.FilmsActivity;
import com.example.mvvm.function.LiveTVActivity;
import com.example.mvvm.function.ShowTimesActivity;
import com.example.mvvm.function.UsersStatusActivity;
import com.example.mvvm.function.WalletActivity;
import com.example.mvvm.model.Fastfood;
import com.example.mvvm.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SettingVM extends ViewModel {
    public ObservableField<Users> users = new ObservableField<>();

    public SettingVM() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(DataLocalManager.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.set(snapshot.getValue(Users.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showDialogDetailUsers(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogDetailUsersBinding binding = CustomDialogDetailUsersBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        Picasso.get().load(users.get().avatar).into(binding.imageAvatar);

        binding.nameUser.setText(users.get().fullname);
        binding.birthdayUser.setText(users.get().birthday);
        binding.emailUser.setText(users.get().email);
        binding.phoneUser.setText(users.get().phonenumber);

        if (users.get().male){
            binding.imageGender.setImageResource(R.drawable.ic_male_24);
        } else {
            binding.imageGender.setImageResource(R.drawable.ic_female_24);
        }
        if (users.get().status){
            binding.lock.setVisibility(View.VISIBLE);
            binding.unlock.setVisibility(View.GONE);
            if (users.get().admin){
                binding.positionUser.setText("Admin");
            }else {
                binding.positionUser.setText("Users");
            }
        }else {

            // chỉ admin được mở khóa cho các user khác
            binding.lock.setVisibility(View.GONE);
            binding.unlock.setVisibility(View.GONE);

            binding.positionUser.setText("Tài khoản bị khóa");
            binding.positionUser.setTextColor(Color.parseColor("#FF0000"));
        }
        binding.changeInf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextIntentUsersInf(view);
            }
        });
        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                CustomDialogChangePasswordBinding binding = CustomDialogChangePasswordBinding.inflate(LayoutInflater.from(view.getContext()));
                dialog.setContentView(binding.getRoot());

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                window.setGravity(Gravity.CENTER);
                dialog.setCancelable(false);

                binding.email.setText(users.get().email);
                binding.push.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.password.getText().length() > 0
                                && binding.passwordNew.getText().length() >= 8
                                && binding.passwordAgain.getText().toString().equals(binding.passwordNew.getText().toString()) )
                        {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(users.get().email, binding.password.getText().toString());
                            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) // Xác thực thành công
                                    {
                                        // Đổi mật khẩu
                                        firebaseUser.updatePassword(binding.passwordNew.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(view.getContext(), "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "Mật khẩu đã được thay đổi");
                                                            onLogOut(view);
                                                        }else {
                                                            Toast.makeText(view.getContext(), "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "Lỗi thay đổi mật khẩu"+ task.getException());
                                                        }
                                                    }
                                                });

                                    }else {
                                        Toast.makeText(view.getContext(), "Xác thực mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Xác thực mật khẩu không thành công"+ task.getException());
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(view.getContext(), "Kiểm tra lại mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                binding.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        binding.lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialogquestion = new Dialog(view.getContext());
                dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                dialogquestion.setContentView(questionBinding.getRoot());

                Window window = dialogquestion.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                window.setGravity(Gravity.CENTER);
                dialogquestion.setCancelable(false);

                questionBinding.textview.setText("Bạn muốn khóa tài khoản");
                questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
                questionBinding.push.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Users").child(DataLocalManager.getUid()).child("status").setValue(false, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Log.d(TAG, "Lock Account : Complete");
                                } else {
                                    Log.d(TAG, "Lock Account : Error - "+error);
                                }
                            }
                        });
                    }
                });
                dialogquestion.show();
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void onNextIntentFilm(View view){
        Intent intent = new Intent(view.getContext(), FilmsActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentWallet(View view){
        Intent intent = new Intent(view.getContext(), WalletActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentShowTimes(View view){
        Intent intent = new Intent(view.getContext(), ShowTimesActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentUsersStatus(View view){
        Intent intent = new Intent(view.getContext(), UsersStatusActivity.class);
        view.getContext().startActivity(intent);
    }

    public void onNextIntentUsersInf(View view){
        Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentFood(View view){
        Intent intent = new Intent(view.getContext(), FastfoodActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onNextIntentLiveTV(View view){
        Intent intent = new Intent(view.getContext(), LiveTVActivity.class);
        view.getContext().startActivity(intent);
    }
    public void onLogOut(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        view.getContext().startActivity(intent);
    }

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl){
        if (imgUrl==null){
            imageView.setImageResource(R.drawable.round_person_pin_24);
        }
        else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}