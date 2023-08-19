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

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.databinding.CustomDialogChangePasswordBinding;
import com.example.mvvm.databinding.CustomDialogDepositMoneyBinding;
import com.example.mvvm.databinding.CustomDialogDetailUsersBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
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
        ObservableField<Users> users = new ObservableField<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.set(snapshot.getValue(Users.class));

                Picasso.get().load(users.get().avatar).into(holder.binding.avatarUser);
                holder.binding.nameUser.setText(""+users.get().fullname);
                holder.binding.emailUser.setText(users.get().email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                CustomDialogDetailUsersBinding binding = CustomDialogDetailUsersBinding.inflate(LayoutInflater.from(view.getContext()));
                dialog.setContentView(binding.getRoot());

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                window.setGravity(Gravity.TOP);
                dialog.setCancelable(false);

                Picasso.get().load(users.get().avatar).into(binding.imageAvatar);

                binding.nameUser.setText(users.get().fullname);
                binding.birthdayUser.setText(users.get().birthday);
                binding.emailUser.setText(users.get().email);
                binding.phoneUser.setText(users.get().phonenumber);

                if (users.get().male){
                    binding.imageGender.setImageResource(R.drawable.ic_male_24);
                } else {
                    binding.imageGender.setImageResource(R.drawable.ic_female_24);
                }
                if (users.get().status){
                    binding.lock.setVisibility(View.VISIBLE);
                    binding.unlock.setVisibility(View.GONE);
                    if (users.get().admin){
                        binding.positionUser.setText("Admin");
                    }else {
                        binding.positionUser.setText("Users");
                    }
                }else {

                    // chỉ admin được mở khóa cho các user khác
                    binding.lock.setVisibility(View.GONE);
                    binding.unlock.setVisibility(View.VISIBLE);

                    binding.positionUser.setText("Tài khoản bị khóa");
                    binding.positionUser.setTextColor(Color.parseColor("#FF0000"));
                }
                binding.changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(view.getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogChangePasswordBinding binding = CustomDialogChangePasswordBinding.inflate(LayoutInflater.from(view.getContext()));
                        dialog.setContentView(binding.getRoot());

                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                        window.setGravity(Gravity.CENTER);
                        dialog.setCancelable(false);

                        binding.email.setText(users.get().email);
                        binding.push.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (binding.password.getText().length() > 0
                                        && binding.passwordNew.getText().length() >= 8
                                        && binding.passwordAgain.getText().toString().equals(binding.passwordNew.getText().toString()) )
                                {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    AuthCredential credential = EmailAuthProvider.getCredential(users.get().email, binding.password.getText().toString());
                                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) // Xác thực thành công
                                            {
                                                // Đổi mật khẩu
                                                firebaseUser.updatePassword(binding.passwordNew.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(view.getContext(), "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, "Mật khẩu đã được thay đổi");
                                                                    Utils.reLoadIntent(view.getContext());
                                                                }else {
                                                                    Toast.makeText(view.getContext(), "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                                                    Log.d(TAG, "Lỗi thay đổi mật khẩu"+ task.getException());
                                                                }
                                                            }
                                                        });

                                            }else {
                                                Toast.makeText(view.getContext(), "Xác thực mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "Xác thực mật khẩu không thành công"+ task.getException());
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(view.getContext(), "Kiểm tra lại mật khẩu", Toast.LENGTH_SHORT).show();
                                }
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
                });
                binding.changeInf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), DetailUsersActivity.class);
                        view.getContext().startActivity(intent);
                    }
                });
                binding.depositIntoWallet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(view.getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogDepositMoneyBinding binding = CustomDialogDepositMoneyBinding.inflate(LayoutInflater.from(view.getContext()));
                        dialog.setContentView(binding.getRoot());

                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                        window.setGravity(Gravity.CENTER);
                        dialog.setCancelable(false);

                        binding.wallet.setText(key);
                        binding.push.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (binding.amountMoney.getText().length()<5)
                                {
                                    Toast.makeText(view.getContext(), "Số tiền nạp phải lớn hơn 9.999 VNĐ",Toast.LENGTH_LONG).show();
                                }else if (binding.description.getText().length() == 0)
                                {
                                    Toast.makeText(view.getContext(), "Nội dung giao dịch",Toast.LENGTH_LONG).show();
                                }else {
                                    TransactionMoney transaction = new TransactionMoney();
                                    transaction.transaction_type = "deposit";
                                    transaction.amount = Integer.parseInt(binding.amountMoney.getText().toString());
                                    transaction.wallet = key;
                                    transaction.supporter = DataLocalManager.getUid();
                                    transaction.description = binding.description.getText().toString();
                                    transaction.time = new SimpleDateFormat("HH:mm:ss").format(Utils.getRealtime());
                                    transaction.date = new SimpleDateFormat("dd/MM/yyyy").format(Utils.getRealtime());
                                    transaction.status = true;
// Open Dialog Verification Deposit
                                    Dialog dialog = new Dialog(view.getContext());
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    CustomDialogQuestionBinding binding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                                    dialog.setContentView(binding.getRoot());

                                    Window window = dialog.getWindow();
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                                    window.setGravity(Gravity.CENTER);
                                    dialog.setCancelable(false);

                                    binding.textview.setText("Bạn muốn nạp "+ Utils.convertPriceToVND(transaction.amount)+" vào địa chỉ ví:" + transaction.wallet);
                                    binding.push.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.child("Transaction").push().setValue(transaction, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    if (error == null) {
                                                        Toast.makeText(view.getContext(), "Nạp thành công", Toast.LENGTH_SHORT).show();
                                                        Utils.reLoadIntent(view.getContext());
                                                    } else {
                                                        Toast.makeText(view.getContext(), "Vui lòng thử lại" + error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
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
                });
                binding.unlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialogquestion = new Dialog(view.getContext());
                        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                        dialogquestion.setContentView(questionBinding.getRoot());

                        Window window = dialogquestion.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                        window.setGravity(Gravity.CENTER);
                        dialogquestion.setCancelable(false);

                        questionBinding.textview.setText("Bạn muốn mở khóa tài khoản");
                        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
                        questionBinding.push.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(key).child("status").setValue(true, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            Log.d(TAG, "Unlock Account : Complete");
                                            Utils.reLoadIntent(view.getContext());
                                        } else {
                                            Log.d(TAG, "Unlock Account : Error - "+error);
                                        }
                                    }
                                });
                            }
                        });
                        dialogquestion.show();
                    }
                });
                binding.lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialogquestion = new Dialog(view.getContext());
                        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                        dialogquestion.setContentView(questionBinding.getRoot());

                        Window window = dialogquestion.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                        window.setGravity(Gravity.CENTER);
                        dialogquestion.setCancelable(false);

                        questionBinding.textview.setText("Bạn muốn khóa tài khoản");
                        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
                        questionBinding.push.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(key).child("status").setValue(false, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            Log.d(TAG, "Lock Account : Complete");
                                            Utils.reLoadIntent(view.getContext());
                                        } else {
                                            Log.d(TAG, "Lock Account : Error - "+error);
                                        }
                                    }
                                });
                            }
                        });
                        dialogquestion.show();
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

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        private ItemUsersBinding binding;

        public UsersViewHolder(@NonNull ItemUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
