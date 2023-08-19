package com.example.mvvm.adapter;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.databinding.CustomDialogChangePasswordBinding;
import com.example.mvvm.databinding.CustomDialogCodeBinding;
import com.example.mvvm.databinding.CustomDialogDepositMoneyBinding;
import com.example.mvvm.databinding.CustomDialogDetailUsersBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.databinding.ItemTransactionBinding;
import com.example.mvvm.databinding.ItemUsersBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.DetailUsersActivity;
import com.example.mvvm.model.TransactionMoney;
import com.example.mvvm.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> implements Filterable {
    List<String> list,list_search;

    public TransactionAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }

    public void setTransactionAdapter(List<String> list) {
        this.list = list;
        this.list_search = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new TransactionAdapter.TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        String  key = list.get(position);

        holder.binding.code.setText("Mã GD: " + key);
        holder.binding.code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showQRCode(v.getContext(), key);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TransactionMoney transaction = snapshot.getValue(TransactionMoney.class);

                holder.binding.time.setText(transaction.time);
                holder.binding.date.setText(transaction.date);

//                Picasso.get().load(users.get().avatar).into(holder.binding.avatarUser);
                if (transaction.transaction_type.equals("deposit")){

                    if (transaction.wallet.equals(transaction.supporter)){
                        holder.binding.type.setText("Nạp online");
                    } else {
                        holder.binding.type.setText("Nạp tại quầy");
                    }

                    holder.binding.content.setVisibility(View.GONE);

                    holder.binding.transactionType.setText("+");

                    holder.binding.transactionType.setTextColor(Color.parseColor("#00FF0A"));
                    holder.binding.amount.setTextColor(Color.parseColor("#00FF0A"));
                }else {

                    holder.binding.content.setVisibility(View.VISIBLE);
                    holder.binding.content.setText("Nội dung giao dịch: \n"+transaction.description);
                    holder.binding.type.setText("Thanh toán");

                    holder.binding.transactionType.setText("-");
                    holder.binding.transactionType.setTextColor(Color.parseColor("#FF0000"));
                    holder.binding.amount.setTextColor(Color.parseColor("#FF0000"));
                }

                holder.binding.amount.setText(Utils.convertPriceToVND(transaction.amount));
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
                                if (Utils.isNumber(keySearch))
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

    public class TransactionViewHolder extends RecyclerView.ViewHolder{
        private ItemTransactionBinding binding;

        public TransactionViewHolder(@NonNull ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
