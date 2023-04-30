package com.example.mvvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemFeedbackBinding;
import com.example.mvvm.databinding.ItemUsersBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    List<Feedback> list;
    Context context;

    public FeedbackAdapter(List<Feedback> list,Context context) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeedbackBinding binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FeedbackAdapter.FeedbackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = list.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(feedback.key_user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.avatar).into(holder.binding.avatarUser);
                holder.binding.nameUser.setText(users.fullname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.comment.setText(feedback.comment);
        holder.binding.time.setText(feedback.time );

    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder{
        private ItemFeedbackBinding binding;

        public FeedbackViewHolder(@NonNull ItemFeedbackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
