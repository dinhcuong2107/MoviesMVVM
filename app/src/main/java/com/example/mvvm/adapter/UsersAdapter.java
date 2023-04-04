package com.example.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.databinding.ItemUsersBinding;
import com.example.mvvm.model.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> implements Filterable {
    List<Users> list,list_search;

    public UsersAdapter(List<Users> list) {
        this.list = list;
        this.list_search = list;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUsersBinding binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UsersAdapter.UsersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Users users = list.get(position);
        if (users == null){return;}
//        Picasso.get().load(films.poster).into(holder.binding.imageViewFilm);
//        holder.binding.textViewnameFilm.setText(""+films.name);
//        holder.binding.nameDirector.setText(films.director);
//        holder.binding.textViewCMT.setText("217");
        holder.binding.emailUser.setText(users.email);
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
                    List<Users> temp = new ArrayList<>();
                    for (Users usersSearch : list_search){
//                        if (usersSearch.getName().toLowerCase().contains(keySearch.toLowerCase())){
//                            temp.add(usersSearch);
//                        }
                    }
                    list = temp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Users>) results.values;
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
