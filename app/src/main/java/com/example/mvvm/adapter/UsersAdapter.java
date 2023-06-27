package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Functions;
import com.example.mvvm.databinding.ItemUsersBinding;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> implements Filterable {
    List<String> list,list_search;

    public UsersAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }

    public void setUsersAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUsersBinding binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UsersAdapter.UsersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        String  key = list.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                Picasso.get().load(users.avatar).into(holder.binding.avatarUser);
                holder.binding.nameUser.setText(""+users.fullname);
                holder.binding.phoneUser.setText(""+users.phonenumber);
                holder.binding.emailUser.setText(users.email);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String keySearch = constraint.toString();
                if (keySearch.isEmpty()){
                    list = list_search;
                }else {
                    List<String> usersList = new ArrayList<>();
                    for (String str : list_search){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(str);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);
                                if (Functions.isNumber(keySearch))
                                {
                                    if (users.phonenumber.toLowerCase().contains(keySearch.toLowerCase())){
                                        usersList.add(str);}
                                }else {
                                    if (users.fullname.toLowerCase().contains(keySearch.toLowerCase())){
                                        usersList.add(str);}
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    list = usersList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        private ItemUsersBinding binding;

        public UsersViewHolder(@NonNull ItemUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
