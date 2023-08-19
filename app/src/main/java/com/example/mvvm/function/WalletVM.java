package com.example.mvvm.function;

import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.TransactionMoney;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WalletVM extends ViewModel {
    public ObservableField<Users> users = new ObservableField<>();
    public ObservableField<String> balance = new ObservableField<>();

    public WalletVM() {
        getDetailUsers();
        Utils.getBalanceFromDatabase(DataLocalManager.getUid(),new Utils.BalanceListener() {
            @Override
            public void onAmountCalculated(int amount) {
                // Xử lý số liệu 'amount'
                balance.set(Utils.convertPriceToVND(amount));
                Log.d("Total Amount", String.valueOf(amount));
            }
        });

    }

    private void getDetailUsers() {
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