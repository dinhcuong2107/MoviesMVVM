package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Utils;
import com.example.mvvm.databinding.ItemFastfoodBinding;
import com.example.mvvm.model.Fastfood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FastfoodAdapter extends RecyclerView.Adapter<FastfoodAdapter.FastfooodViewHolder> {
    List<String> list;

    public FastfoodAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<String> getListSelect(){
        return list;
    }

    @NonNull
    @Override
    public FastfooodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFastfoodBinding binding = ItemFastfoodBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FastfoodAdapter.FastfooodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FastfooodViewHolder holder, int position) {
        // cho phép di chuyển nội dung hiển thị trên TextView
        holder.binding.textname.setSelected(true);

        String key = list.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fastfood").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fastfood fastfood = snapshot.getValue(Fastfood.class);
                Picasso.get().load(fastfood.image).into(holder.binding.image);
                holder.binding.textname.setText(fastfood.name);
                holder.binding.textprice.setText(Utils.convertPriceToVND(fastfood.price));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.layout.setVisibility(View.VISIBLE);
            }
        });

        holder.binding.upquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.binding.quantity.getText().toString());
                quantity++;
                holder.binding.quantity.setText(""+quantity);
            }
        });
        holder.binding.downquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(holder.binding.quantity.getText().toString());
                if (quantity == 0)
                {
                    quantity = 0;
                    holder.binding.layout.setVisibility(View.GONE);
                }else {
                    quantity--;
                }
                holder.binding.quantity.setText(""+quantity);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){return list.size();}
        return 0;
    }
    public void setFastfoodAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class FastfooodViewHolder extends RecyclerView.ViewHolder{
        private ItemFastfoodBinding binding;

        public FastfooodViewHolder(@NonNull ItemFastfoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
