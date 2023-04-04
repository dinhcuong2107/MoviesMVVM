package com.example.mvvm.function;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailUsersVM extends BaseObservable {
    public Users users;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public DetailUsersVM() {
        users = new Users();
        if (firebaseUser != null) {
            users.email = firebaseUser.getEmail();
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users m_users = new Users();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String Uid = dataSnapshot.getKey();
                    if (Uid.equals(DataLocalManager.getUid())) {
                        m_users = dataSnapshot.getValue(Users.class);
                    }
                }
                if (m_users.email != null) {
                    setUsers(m_users);
                    setUsersAvatar(m_users.avatar);
                    setUsersBirthday(m_users.birthday);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Bindable
    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
        notifyPropertyChanged(BR.users);
    }

    @Bindable
    public String getUsersAvatar() {
        return users.getAvatar();
    }

    public void setUsersAvatar(String avatar) {
        users.setAvatar(avatar);
        notifyPropertyChanged(BR.usersAvatar);
    }

    @Bindable
    public String getUsersBirthday() {
        return users.getBirthday();
    }

    public void setUsersBirthday(String birthday) {
        users.setBirthday(birthday);
        notifyPropertyChanged(BR.usersBirthday);
    }

    public void onclickMale() {
        users.male = true;
    }

    public void onclickFemale() {
        users.male = false;
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
                setUsersBirthday(simpleDateFormat.format(calendar.getTime()).toString());
            }
        }, year, month, day);
        pickerDialog.show();
    }

    public void onclickRegister(View view) {
        String error = "";

        if (users.phonenumber == null) {
            error = "Bổ sung số điện thoại";
        } else if (users.phonenumber.length() != 10) {
            error = "Số điện thoại Không hợp lệ (!=10)";
        } else {
            Pattern pattern = Pattern.compile("0\\d{9}");
            Matcher matcher = pattern.matcher(users.phonenumber);
            int index = users.phonenumber.charAt(1);
            // "0" charAt = 48
            if (matcher.matches() && index != 48) {
            } else {
                error = "Số điện thoại không đúng định dạng";
            }
        }
        if (users.birthday == null) {
            error = "Bổ sung ngày sinh";
        }
        if (users.male == null) {
            error = "Bổ sung giới tính";
        }
        if (users.fullname == null) {
            error = "Bổ sung Họ và Tên";
        } else if (users.fullname.length() < 5) {
            error = "Kiểm tra lại Họ và Tên";
        }
        if (users.avatar == null) {
            error = "Bổ sung ảnh đại diện";
        }

        if (error.length() > 1) {
            Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
        } else {
            completeRegister(view);
        }
    }

    private void completeRegister(View view) {
        users.admin = false;
        users.status = true;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(firebaseUser.getUid()).setValue(users, new DatabaseReference.CompletionListener() {
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

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.round_person_pin_24);
        } else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}