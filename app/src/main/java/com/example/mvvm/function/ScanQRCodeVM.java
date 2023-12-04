package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.LoginActivity;
import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.adapter.ProductsAdapter;
import com.example.mvvm.databinding.CustomDialogChangePasswordBinding;
import com.example.mvvm.databinding.CustomDialogDepositMoneyBinding;
import com.example.mvvm.databinding.CustomDialogDetailTransactionBinding;
import com.example.mvvm.databinding.CustomDialogDetailUsersBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.databinding.CustomDialogTicketVerificationBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.Movies;
import com.example.mvvm.model.Products;
import com.example.mvvm.model.SeriesFilms;
import com.example.mvvm.model.Ticket;
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

public class ScanQRCodeVM extends ViewModel {
    public ObservableField<String> code = new ObservableField<>();
    public ObservableField<String> text = new ObservableField<>();
    public ObservableField<Integer> typeCheck = new ObservableField<>();
    private MutableLiveData<Context> context = new MutableLiveData<>();

    public ScanQRCodeVM() {
    }

    public void setCode(String string) {
        code.set(string);
        
        if (typeCheck.get() == 0) {
            dialogTicket();
        } else if (typeCheck.get() == 1) {
            dialogTransaction();
        } else if (typeCheck.get() == 2) {
            dialogUsers();
        }
        
    }

    private void dialogUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(code.get());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users users = snapshot.getValue(Users.class);

                        Dialog dialog = new Dialog(context.getValue());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogDetailUsersBinding binding = CustomDialogDetailUsersBinding.inflate(LayoutInflater.from(context.getValue()));
                        dialog.setContentView(binding.getRoot());

                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                        window.setGravity(Gravity.TOP);
                        dialog.setCancelable(false);

                        Picasso.get().load(users.avatar).into(binding.imageAvatar);

                        binding.nameUser.setText(users.fullname);
                        binding.birthdayUser.setText(users.birthday);
                        binding.emailUser.setText(users.email);
                        binding.phoneUser.setText(users.phonenumber);

                        if (users.male){
                            binding.imageGender.setImageResource(R.drawable.ic_male_24);
                        } else {
                            binding.imageGender.setImageResource(R.drawable.ic_female_24);
                        }
                        if (users.status){
                            binding.lock.setVisibility(View.VISIBLE);
                            binding.unlock.setVisibility(View.GONE);
                            if (users.admin){
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
                                Dialog dialog = new Dialog(context.getValue());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                CustomDialogChangePasswordBinding binding = CustomDialogChangePasswordBinding.inflate(LayoutInflater.from(context.getValue()));
                                dialog.setContentView(binding.getRoot());

                                Window window = dialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                                window.setGravity(Gravity.CENTER);
                                dialog.setCancelable(false);

                                binding.email.setText(users.email);
                                binding.push.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (binding.password.getText().length() > 0
                                                && binding.passwordNew.getText().length() >= 8
                                                && binding.passwordAgain.getText().toString().equals(binding.passwordNew.getText().toString()) )
                                        {
                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            AuthCredential credential = EmailAuthProvider.getCredential(users.email, binding.password.getText().toString());
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
                                                                            Toast.makeText(context.getValue(), "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                                                            Log.d(TAG, "Mật khẩu đã được thay đổi");

                                                                            Intent intent = new Intent(v.getContext(), LoginActivity.class);
                                                                            v.getContext().startActivity(intent);

                                                                        }else {
                                                                            Toast.makeText(context.getValue(), "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                                                            Log.d(TAG, "Lỗi thay đổi mật khẩu"+ task.getException());
                                                                        }
                                                                    }
                                                                });

                                                    }else {
                                                        Toast.makeText(context.getValue(), "Xác thực mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Xác thực mật khẩu không thành công"+ task.getException());
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(context.getValue(), "Kiểm tra lại mật khẩu", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(context.getValue(), DetailUsersActivity.class);
                                context.getValue().startActivity(intent);
                            }
                        });
                        binding.depositIntoWallet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog dialog = new Dialog(context.getValue());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                CustomDialogDepositMoneyBinding binding = CustomDialogDepositMoneyBinding.inflate(LayoutInflater.from(context.getValue()));
                                dialog.setContentView(binding.getRoot());

                                Window window = dialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                                window.setGravity(Gravity.CENTER);
                                dialog.setCancelable(false);

                                binding.wallet.setText(code.get());
                                binding.push.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (binding.amountMoney.getText().length()<5)
                                        {
                                            Toast.makeText(context.getValue(), "Số tiền nạp phải lớn hơn 9.999 VNĐ",Toast.LENGTH_LONG).show();
                                        }else if (binding.description.getText().length() == 0)
                                        {
                                            Toast.makeText(context.getValue(), "Nội dung giao dịch",Toast.LENGTH_LONG).show();
                                        }else {
                                            TransactionMoney transaction = new TransactionMoney();
                                            transaction.transaction_type = "deposit";
                                            transaction.amount = Integer.parseInt(binding.amountMoney.getText().toString());
                                            transaction.wallet = code.get();
                                            transaction.supporter = DataLocalManager.getUid();
                                            transaction.description = binding.description.getText().toString();
                                            transaction.time = new SimpleDateFormat("HH:mm:ss").format(Utils.getRealtime());
                                            transaction.date = new SimpleDateFormat("dd/MM/yyyy").format(Utils.getRealtime());
                                            transaction.status = true;

                                            // Open Dialog Verification Deposit
                                            Dialog dialog = new Dialog(context.getValue());
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            CustomDialogQuestionBinding binding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(context.getValue()));
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
                                                                Toast.makeText(context.getValue(), "Nạp thành công", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(v.getContext(), UsersStatusActivity.class);
                                                                v.getContext().startActivity(intent);
                                                            } else {
                                                                Toast.makeText(context.getValue(), "Vui lòng thử lại" + error, Toast.LENGTH_SHORT).show();
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
                                Dialog dialogquestion = new Dialog(context.getValue());
                                dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(context.getValue()));
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
                                        databaseReference.child("Users").child(code.get()).child("status").setValue(true, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                if (error == null) {
                                                    Log.d(TAG, "Unlock Account : Complete");

                                                    Intent intent = new Intent(v.getContext(), UsersStatusActivity.class);
                                                    v.getContext().startActivity(intent);
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
                                Dialog dialogquestion = new Dialog(context.getValue());
                                dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(context.getValue()));
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
                                        databaseReference.child("Users").child(code.get()).child("status").setValue(false, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                if (error == null) {
                                                    Log.d(TAG, "Lock Account : Complete");

                                                    Intent intent = new Intent(v.getContext(), UsersStatusActivity.class);
                                                    v.getContext().startActivity(intent);
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
                } else {
                    // Tham chiếu "Users" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                    Toast.makeText(context.getValue(), "Mã QR không hợp lệ",Toast. LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogTransaction() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction").child(code.get());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TransactionMoney transaction = snapshot.getValue(TransactionMoney.class);

                    Dialog dialog = new Dialog(context.getValue());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    CustomDialogDetailTransactionBinding binding = CustomDialogDetailTransactionBinding.inflate(LayoutInflater.from(context.getValue()));
                    dialog.setContentView(binding.getRoot());

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
                    window.setGravity(Gravity.BOTTOM);


                    binding.transactionType.setText(transaction.transaction_type);
                    binding.time.setText(transaction.time + " "+ transaction.date);

                    DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(transaction.supporter);
                    databaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            binding.supporter.setText(users.fullname);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // deposit and payment
                    if (transaction.transaction_type.equals("payment")) {
                        binding.amount.setText(" - " + Utils.convertPriceToVND(transaction.amount));
                        binding.amount.setTextColor(Color.parseColor("#E91E63"));

                        if (transaction.description.substring(0, Math.min(transaction.description.length(), 12)).equals("Movie Store:")) {

                            DatabaseReference referenceSeries = FirebaseDatabase.getInstance().getReference("Series Films").child(transaction.description.substring(12));
                            referenceSeries.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Tham chiếu "Series Films" tồn tại, tiếp tục với logic của bạn
                                        SeriesFilms seriesFilms = snapshot.getValue(SeriesFilms.class);

                                        String detail = "Series Films: " + seriesFilms.name +"\nSố tập: "+Utils.convertStringToList(seriesFilms.epsodecode).size() + "/" + seriesFilms.totalEpisode;

                                        binding.detail.setText(detail);
                                    } else {
                                        // Tham chiếu "Series Films" không tồn tại
                                        // Xử lý tình huống này theo ý muốn của bạn
                                        // ...

                                        DatabaseReference referenceFilms = FirebaseDatabase.getInstance().getReference("Films").child(transaction.description.substring(12));
                                        referenceFilms.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    // Tham chiếu "Films" tồn tại, tiếp tục với logic của bạn
                                                    Films films = snapshot.getValue(Films.class);

                                                    String detail = "Films: " + films.name + " - " + films.year +
                                                            "\nĐạo diễn: "+films.director +
                                                            "\nDiễn viên chính: "+films.main_actors +
                                                            "\nQuốc gia: "+films.country;

                                                    binding.detail.setText(detail);

                                                } else {
                                                    // Tham chiếu "Films" không tồn tại
                                                    // Xử lý tình huống này theo ý muốn của bạn
                                                    // ...
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        } else if (transaction.description.substring(0, Math.min(transaction.description.length(), 13)).equals("Ticket Store:")) {
                            //
                            binding.detail.setText(transaction.description.substring(13));
                        }

                    }else {
                        binding.amount.setText(" + " + Utils.convertPriceToVND(transaction.amount));
                        binding.amount.setTextColor(Color.parseColor("#009688"));

                        if (transaction.wallet.equals(transaction.supporter)) {
                            binding.detail.setText("Nạp online");
                        } else {
                            binding.detail.setText("Nạp offline tại quầy");
                        }
                    }

                    if (transaction.status){
                        binding.status.setText("Hoàn tất");
                    }else {
                        binding.status.setText("giao dịch không thành công");
                    }


                    binding.wallet.setText(transaction.wallet);
                    binding.supporter.setText(transaction.supporter);
                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {
                    // Tham chiếu "Series Films" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                    Toast.makeText(context.getValue(), "Mã QR không hợp lệ",Toast. LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dialogTicket() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ticket").child(code.get());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ticket ticket = snapshot.getValue(Ticket.class);

                DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(ticket.key_user);
                databaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        Dialog dialog = new Dialog(context.getValue());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        CustomDialogTicketVerificationBinding binding = CustomDialogTicketVerificationBinding.inflate(LayoutInflater.from(context.getValue()));
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

                        DatabaseReference databaseMovies = FirebaseDatabase.getInstance().getReference("Movies").child(ticket.key_movies);
                        databaseMovies.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Movies movies = snapshot.getValue(Movies.class);

                                DatabaseReference databaseFilms = FirebaseDatabase.getInstance().getReference("Films").child(movies.key_film);
                                databaseFilms.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Films films = snapshot.getValue(Films.class);
                                        binding.films.setText(films.name);

                                        binding.seat.setText(movies.cinema + " " +ticket.seat);
                                        binding.films.setText(films.name);
                                        binding.price.setText(Utils.convertPriceToVND(movies.price));
                                        binding.quantity.setText(""+ Utils.convertStringToList(ticket.seat).size());
                                        binding.money.setText(Utils.convertPriceToVND(movies.price* Utils.convertStringToList(ticket.seat).size()));
                                        binding.timeNow.setText(ticket.time);
                                        binding.time.setText(movies.time + " " + movies.date);
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



                        for (int i = 0; i< Utils.convertStringToList(ticket.key_fastfood).size(); i++)
                        {
                            list.add(new Products(Utils.convertStringToList(ticket.key_fastfood).get(i),Integer.parseInt(Utils.convertStringToList(ticket.quantity_fastfood).get(i))));
                        }

                        ProductsAdapter adapter = new ProductsAdapter(list);
                        binding.recyclerviewverification.setAdapter(adapter);

                        binding.fullname.setText(users.fullname);
                        binding.keyuser.setText(ticket.key_user);

                        if (ticket.status == false){
                            binding.cancel.setText("Đã hủy - "+ ticket.time);
                            binding.pay.setVisibility(View.GONE);
                        }else {
                            binding.pay.setVisibility(View.VISIBLE);
                            binding.pay.setText("Xác nhận");
                        }


                        binding.progressbar.setVisibility(View.GONE);

                        binding.total.setText(Utils.convertPriceToVND(ticket.price));
                        binding.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (ticket.time.length() < 20) {
                            binding.pay.setVisibility(View.VISIBLE);
                        }else {
                            binding.pay.setVisibility(View.GONE);
                        }
                        binding.pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String s = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Utils.getRealtime());
                                ticket.time = ticket.time + " - Check in: " + s;

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Ticket").child(code.get()).child("time").setValue(ticket.time, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            Toast.makeText(context.getValue(), "Check in thành công " + s, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context.getValue(), ScanQRCodeActivity.class);
                                            context.getValue().startActivity(intent);
                                        } else {
                                            Toast.makeText(context.getValue(), "" + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        dialog.show();
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
    }

    public void setTypeCheck(View view, Integer i) {
        typeCheck.set(i);
        context.setValue(view.getContext());
    }
}