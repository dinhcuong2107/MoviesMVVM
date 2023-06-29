package com.example.mvvm.adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Utils;
import com.example.mvvm.R;
import com.example.mvvm.databinding.CustomDialogTicketVerificationBinding;
import com.example.mvvm.databinding.ItemTicketBinding;
import com.example.mvvm.datalocal.DataLocalManager;
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

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    List<String> list;
    Ticket ticket = new Ticket();
    Movies movies = new Movies();
    Films films = new Films();
    Users users = new Users();

    public TicketAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<String> getListSelect(){
        return list;
    }

    @NonNull
    @Override
    public TicketAdapter.TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new TicketAdapter.TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.TicketViewHolder holder, int position) {
        String key = list.get(position);

        holder.binding.ticket.setText(key);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ticket").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticket = snapshot.getValue(Ticket.class);
                holder.binding.ticket.setText(key);
                DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies").child(ticket.key_movies);
                databaseMovies.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        movies = snapshot.getValue(Movies.class);
                        holder.binding.time.setText(movies.time);
                        holder.binding.date.setText(movies.date);

                        DatabaseReference databaseFilms = FirebaseDatabase.getInstance().getReference("Films").child(movies.key_film);
                        databaseFilms.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                films = snapshot.getValue(Films.class);
                                holder.binding.films.setText(films.name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.binding.detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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



                        for (int i = 0; i< Utils.convertStringToList(ticket.key_fastfood).size(); i++)
                        {
                            list.add(new Products(Utils.convertStringToList(ticket.key_fastfood).get(i),Integer.parseInt(Utils.convertStringToList(ticket.quantity_fastfood).get(i))));
                        }

                        ProductsAdapter adapter = new ProductsAdapter(list);
                        binding.recyclerviewverification.setAdapter(adapter);

                        if (DataLocalManager.getAdmin()){
                        }else {
                            binding.keyuser.setText(ticket.key_user);
                            DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(ticket.key_user);
                            databaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    users = snapshot.getValue(Users.class);
                                    binding.fullname.setText(users.fullname);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        if (ticket.status == false){
                            binding.cancel.setText("Đã hủy - "+ ticket.time);
                        }

                        binding.seat.setText(movies.cinema + " " +ticket.seat);
                        binding.films.setText(films.name);
                        binding.price.setText(Utils.convertPriceToVND(movies.price));
                        binding.quantity.setText(""+ Utils.convertStringToList(ticket.seat).size());
                        binding.money.setText(Utils.convertPriceToVND(movies.price* Utils.convertStringToList(ticket.seat).size()));
                        binding.timeNow.setText(ticket.time);
                        binding.time.setText(movies.time + " " + movies.date);

                        binding.progressbar.setVisibility(View.GONE);
                        binding.pay.setVisibility(View.GONE);
                        binding.total.setText(Utils.convertPriceToVND(ticket.price));
                        binding.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }
    public void setTicketAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder{
        private ItemTicketBinding binding;

        public TicketViewHolder(@NonNull ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
