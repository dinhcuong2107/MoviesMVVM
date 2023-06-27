package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.Functions;
import com.example.mvvm.databinding.ItemFastfoodBinding;
import com.example.mvvm.databinding.ItemProductsBinding;
import com.example.mvvm.model.Fastfood;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.example.mvvm.model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {
    private List<Products> list;

    public ProductsAdapter(List<Products> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductsBinding binding = ItemProductsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ProductsAdapter.ProductsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Products products = list.get(position);
        DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Fastfood").child(products.key_product);
        databaseMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Fastfood fastfood = snapshot.getValue(Fastfood.class);

                holder.binding.name.setText(fastfood.name);
                holder.binding.price.setText(Functions.convertPriceToVND(fastfood.price));
                holder.binding.quantity.setText(""+products.quantity);
                holder.binding.money.setText(Functions.convertPriceToVND(fastfood.price*products.quantity));
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
    public void setProducrsAdapter(List<Products> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder{
        private ItemProductsBinding binding;

        public ProductsViewHolder(@NonNull ItemProductsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
