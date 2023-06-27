package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvvm.R;
import com.example.mvvm.adapter.FastfoodAdapter;
import com.example.mvvm.databinding.ActivityDetailTicketBinding;
import com.example.mvvm.model.Fastfood;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.ViewModelProviders;
public class DetailTicketActivity extends AppCompatActivity {
    String key_movies;
    private DetailTicketVM detailTicketVM;
    FastfoodAdapter fastfoodAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailTicketBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_ticket);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        key_movies = intent.getStringExtra("key_movies");

        detailTicketVM = new ViewModelProvider(this).get(DetailTicketVM.class);
        detailTicketVM.init(key_movies);
        binding.setDetailticket(detailTicketVM);
        binding.executePendingBindings();

//        binding.test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < binding.recyclerview.getChildCount(); i++){
//                    // lấy vị trí item tương ứng với i
//                    View itemview = binding.recyclerview.getChildAt(i);
//                    // lấy dữ liệu từ view
//                    TextView textView = itemview.findViewById(R.id.quantity);
//                    TextView textName = itemview.findViewById(R.id.textname);
//                    int num = Integer.parseInt(textView.getText().toString());
//
//                    if (num > 0){
//                        Toast.makeText(DetailTicketActivity.this, ""+textName.getText().toString()+" :"+num, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
    }
}