package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Functions;
import com.example.mvvm.LoginActivity;
import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.SplashScreenActivity;
import com.example.mvvm.adapter.FastfoodAdapter;
import com.example.mvvm.adapter.ProductsAdapter;
import com.example.mvvm.databinding.CustomDialogFastfoodBinding;
import com.example.mvvm.databinding.CustomDialogTicketVerificationBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.livedata.FastfoodLiveData;
import com.example.mvvm.model.Fastfood;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.example.mvvm.model.Products;
import com.example.mvvm.model.Ticket;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailTicketVM extends ViewModel {
    public ObservableField<Boolean> select = new ObservableField<>();
    private String key_movies;
    public ObservableField<Users> usersObservableField = new ObservableField<>();
    public ObservableField<Films> filmsObservableField = new ObservableField<>();
    public ObservableField<Movies> moviesObservableField = new ObservableField<>();
    public ObservableArrayList<String> listseat = new ObservableArrayList<>();
    public ObservableArrayList<Products> productsList = new ObservableArrayList<>();
    public ObservableArrayList<Boolean> listseatenable = new ObservableArrayList<>();

    public void init(String string) {
        key_movies = string;
        if (select.get() == null) { select.set(true);}

        loadSeatEnable();

        DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies").child(key_movies);
        databaseMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moviesObservableField.set(snapshot.getValue(Movies.class));

                DatabaseReference databaseFilms = FirebaseDatabase.getInstance().getReference("Films").child(moviesObservableField.get().key_film);
                databaseFilms.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        filmsObservableField.set(snapshot.getValue(Films.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(DataLocalManager.getUid());
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersObservableField.set(snapshot.getValue(Users.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadSeatEnable() {
        if (listseatenable.size() == 0){
            for (int i=0; i<86; i++)
            {
                listseatenable.add(true);
            }
        }

        DatabaseReference databaseSeatEnable = FirebaseDatabase.getInstance().getReference("Ticket");
        databaseSeatEnable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Ticket temp = dataSnapshot.getValue(Ticket.class);
                    if (temp.key_movies.equals(key_movies))
                    {
                        list.addAll(Functions.convertStringToList(temp.seat));
                    }
                }

                for (int i=0; i<list.size(); i++)
                {
                    listseatenable.set(Functions.convertSeat(list.get(i).replace(" ","")),false);
                    Log.d(TAG, "onDataChange: " + Functions.convertSeat(list.get(i).replace(" ","")));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clickseclect(){
            if (select.get())
            {
                select.set(false);
            }else {
                select.set(true);
            }
    }
    public void onClickDialogFastfood(View view){
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogFastfoodBinding binding = CustomDialogFastfoodBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
        window.setGravity(Gravity.BOTTOM);

        // setup RecycleView Fastfood
        GridLayoutManager layoutManager = new GridLayoutManager(binding.recyclerview.getContext(), 3);
        binding.recyclerview.setLayoutManager(layoutManager);

        List<String> list = new ArrayList<>();

        FastfoodAdapter fastfoodAdapter = new FastfoodAdapter(new ArrayList<>());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fastfood");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Fastfood temp = dataSnapshot.getValue(Fastfood.class);
                    if (temp.status){
                        list.add(dataSnapshot.getKey());
                    }
                }

                fastfoodAdapter.setFastfoodAdapter(list);
                binding.recyclerview.setAdapter(fastfoodAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productsList.clear();
                FastfoodAdapter adapter = (FastfoodAdapter) binding.recyclerview.getAdapter();
                if (adapter != null){
                    for (int i = 0; i< list.size(); i ++)
                    {
                        RecyclerView.ViewHolder viewHolder = binding.recyclerview.findViewHolderForAdapterPosition(i);
                        if (viewHolder != null)
                        {
                            TextView textView = viewHolder.itemView.findViewById(R.id.quantity);
                            int num = Integer.parseInt(textView.getText().toString());
                            if (num>0)
                            {
                                productsList.add(new Products(fastfoodAdapter.getListSelect().get(viewHolder.getAdapterPosition()), num));
                            }

                        }
                    }

                }
                dialog.dismiss();
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
    public void onClickDialogVerification(View view){
        if (listseat.size() == 0){
            Toast.makeText(view.getContext(), "Vui lòng chọn ghế (vị trí)", Toast.LENGTH_SHORT).show();
        }else {
            Ticket ticket = new Ticket();
            ticket.key_movies = key_movies;
            ticket.key_user = DataLocalManager.getUid();
            ticket.paper = DataLocalManager.getAdmin();
            ticket.key_fastfood = null;
            ticket.quantity_fastfood = null;
            Collections.sort(listseat, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.substring(0,1).compareTo(s2.substring(0,1));
                }
            });

            ticket.seat = listseat.toString();
            ticket.time = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Functions.getRealtime());
            ticket.status = true;

            Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            CustomDialogTicketVerificationBinding binding = CustomDialogTicketVerificationBinding.inflate(LayoutInflater.from(view.getContext()));
            dialog.setContentView(binding.getRoot());

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
            window.setGravity(Gravity.BOTTOM);

            // setup RecycleView
            ArrayList<Products> list = new ArrayList<>();
            LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerviewverification.getContext(), RecyclerView.VERTICAL,false);
            binding.recyclerviewverification.setLayoutManager(layoutManager);

            for (int i=0; i<productsList.size();i++)
            {
                list.add(productsList.get(i));
            }

            ProductsAdapter adapter = new ProductsAdapter(list);
            binding.recyclerviewverification.setAdapter(adapter);

            if (DataLocalManager.getAdmin()){
            }else {
                binding.fullname.setText(usersObservableField.get().fullname);
                binding.keyuser.setText(ticket.key_user);
            }

            binding.seat.setText(moviesObservableField.get().cinema + " " +ticket.seat);
            binding.films.setText(filmsObservableField.get().name);
            binding.price.setText(Functions.convertPriceToVND(moviesObservableField.get().price));
            binding.quantity.setText(""+listseat.size());
            binding.money.setText(Functions.convertPriceToVND(moviesObservableField.get().price*listseat.size()));
            binding.timeNow.setText(ticket.time);
            binding.time.setText(moviesObservableField.get().time + " " + moviesObservableField.get().date);

            binding.progressbar.setVisibility(View.VISIBLE);
            android.os.Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.progressbar.setVisibility(View.GONE);
                    int total = moviesObservableField.get().price*listseat.size();
                    if (productsList.size() != 0){
                        for (int i = 0; i < productsList.size(); i++){
                            // lấy vị trí item tương ứng với i
                            View itemview = binding.recyclerviewverification.getChildAt(i);
                            // lấy dữ liệu từ view
                            TextView textView = itemview.findViewById(R.id.money);
                            int num = Functions.convertVNDToPrice(textView.getText().toString());
                            total = total + num;
                        }
                        binding.total.setText(Functions.convertPriceToVND(total));
                    }else {
                        binding.total.setText(Functions.convertPriceToVND(total));
                    }
                    ticket.price = total;
                }
            },1000);
            binding.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            binding.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productsList.size()>0){
                        List<String> keyfastfood = new ArrayList<>();
                        List<Integer> quantityfastfood = new ArrayList<>();
                        for (int i=0; i<productsList.size();i++)
                        {
                            keyfastfood.add(productsList.get(i).key_product);
                            quantityfastfood.add(productsList.get(i).quantity);
                        }
                        ticket.quantity_fastfood = quantityfastfood.toString();
                        ticket.key_fastfood = keyfastfood.toString();
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Ticket").push().setValue(ticket, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                Toast.makeText(view.getContext(), "Mua vé thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                view.getContext().startActivity(intent);
                            } else {
                                Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            dialog.show();

        }
    }

    public void onclickaddseat(View view){
        Button button = (Button) view;
        if (listseatenable.get(Functions.convertSeat(button.getText().toString())))
        {
            if (listseat.size() == 0)
            {
                listseat.add(button.getText().toString());
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_y));
            }else {
                boolean enable = true;
                for (int i =0; i<listseat.size();i++){
                    if (listseat.get(i).equals(button.getText().toString())){
                        listseat.remove(i);
                        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.transparent));
                        enable = false;
                    }
                }
                if (enable){
                    listseat.add(button.getText().toString());
                    view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.color_y));
                }
            }
        }else {
            Toast.makeText(view.getContext(), "Ghế ("+ button.getText().toString() + ") đã được đặt trước. Chọn vị trí khác!", Toast.LENGTH_SHORT).show();
        }
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