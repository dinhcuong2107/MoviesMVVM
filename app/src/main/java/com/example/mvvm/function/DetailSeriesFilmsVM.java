package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.adapter.FeedbackAdapter;
import com.example.mvvm.databinding.CustomDialogFeedbackBinding;
import com.example.mvvm.databinding.CustomDialogOnlineFilmsBinding;
import com.example.mvvm.databinding.CustomDialogQuestionBinding;
import com.example.mvvm.databinding.CustomDialogSuccessBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.SeriesFilms;
import com.example.mvvm.model.TransactionMoney;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailSeriesFilmsVM extends ViewModel {
    public MutableLiveData<List<String>> list_epsode = new MutableLiveData<>();

    public ObservableField<Boolean> admin = new ObservableField<>();
    public ObservableField<Boolean> bought_epsode = new ObservableField<>();
    public ObservableField<Boolean> bought_series = new ObservableField<>();
    public ObservableField<String> key_epsode = new ObservableField<>();
    public ObservableField<Integer> max_epsode = new ObservableField<>();
    public ObservableField<Integer> epsode = new ObservableField<>();
    public ObservableField<String> key_series = new ObservableField<>();
    public ObservableField<String> key_feedback = new ObservableField<>();
    public ObservableField<String> quantityLike = new ObservableField<>();
    public ObservableField<String> quantityComment = new ObservableField<>();
    public ObservableField<Boolean> feeling = new ObservableField<>();
    public ObservableField<Boolean> isNew = new ObservableField<>();
    public ObservableField<Boolean> isHot = new ObservableField<>();
    public ObservableField<Boolean> isOnline = new ObservableField<>();

    public ObservableField<Films> films = new ObservableField<>();
    public ObservableField<SeriesFilms> seriesfilms = new ObservableField<>();

    // trả về giá dạng xxx.xxx.xxx VNĐ
    public ObservableField<String> price = new ObservableField<>();
    public ObservableField<Integer> price_epsode = new ObservableField<>();

    public DetailSeriesFilmsVM () {
        admin.set(DataLocalManager.getAdmin());
    }

    public MutableLiveData<List<String>> getLiveData() {
        if (list_epsode == null) {
            list_epsode = new MutableLiveData<List<String>>();
            list_epsode.setValue(new ArrayList<>());
        }
        return list_epsode;
    }

    public void setLiveData(MutableLiveData<List<String>> liveData) {
        this.list_epsode = liveData;
    }

    public void setKey_series(String s){
        key_series.set(s);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Series Films").child(s);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seriesfilms.set(snapshot.getValue(SeriesFilms.class));
                list_epsode.setValue(Utils.convertStringToList(seriesfilms.get().epsodecode));

                price.set(Utils.convertPriceToVND(seriesfilms.get().price));
                max_epsode.set(list_epsode.getValue().size());

                // set epsode default
                load_epsode(0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void load_epsode(int i) {
        epsode.set(i+1);
        key_epsode.set(list_epsode.getValue().get(i));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Films").child(list_epsode.getValue().get(i));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                films.set(snapshot.getValue(Films.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkNew();
        checkHot();
        checkOnline();
        countComment();

        checkBoughtEpsode();
        checkBoughtSeries();
    }
    public void click_buy_series(View view) {
        if (!admin.get()){
            if (bought_series.get()){
                // show dialog
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie Store").child(DataLocalManager.getUid()).child(key_series.get());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Tham chiếu "Movie Store" tồn tại
                            // ...
                            Utils.showError(view.getContext(), "Phim đã được mua vào "+ snapshot.getValue());
                        } else {
                            // Tham chiếu "Movie Store" không tồn tại
                            // Xử lý tình huống này theo ý muốn của bạn
                            // ...
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else {
                // show dialog buy series
                openDialogMovieStore(view);
            }
        }
    }

    public void click_play(View view) {
        if (admin.get()){
            Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
            intent.putExtra("video", films.get().video);
            intent.putExtra("name", films.get().name);
            view.getContext().startActivity(intent);
        }else {
            if (!isOnline.get()){
                Utils.showError(view.getContext(), "Tập phim chưa được công chiếu online");
            }else {

                if (films.get().status) {
                    if ( price_epsode.get()==0){
                        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
                        intent.putExtra("video", films.get().video);
                        intent.putExtra("name", films.get().name);
                        view.getContext().startActivity(intent);
                    }else {
                        if (bought_series.get() || bought_epsode.get()){
                            Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
                            intent.putExtra("video", films.get().video);
                            intent.putExtra("name", films.get().name);
                            view.getContext().startActivity(intent);
                        }else {
                            Utils.showError(view.getContext(), "Tập phim yêu cầu trả phí");
                        }
                    }
                } else {
                    Utils.showError(view.getContext(), "Tập phim đã ngừng công chiếu");
                }
            }



        }
    }

    public void click_fix(View view) {
        Intent intent = new Intent(view.getContext(), AddSeriesFilmsActivity.class);
        intent.putExtra("key_series", key_series.get());
        view.getContext().startActivity(intent);
    }
    private void openDialogMovieStore(View view) {
        Utils.getBalanceFromDatabase(DataLocalManager.getUid(),new Utils.BalanceListener() {
            @Override
            public void onAmountCalculated(int amount) {
                // Xử lý số liệu 'amount'
                Log.d("Total Amount", String.valueOf(amount));

                if (amount >= Utils.convertVNDToPrice(price.get()))
                {
                    Dialog dialogquestion = new Dialog(view.getContext());
                    dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    CustomDialogSuccessBinding questionBinding = CustomDialogSuccessBinding.inflate(LayoutInflater.from(view.getContext()));
                    dialogquestion.setContentView(questionBinding.getRoot());

                    Window window = dialogquestion.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                    window.setGravity(Gravity.CENTER);
                    dialogquestion.setCancelable(false);
                    questionBinding.textview.setText("Bạn muốn thêm bộ phim '"+ seriesfilms.get().name +"' vào Store với giá " + price.get());
                    questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());

                    questionBinding.push.setText("Thanh toán và xem ngay");
                    questionBinding.push.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String time = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Utils.getRealtime());

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Movie Store").child(DataLocalManager.getUid()).child(key_series.get()).setValue(time, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        paymment(view);
                                        Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    dialogquestion.show();
                }else {
                    Utils.showError(view.getContext(), "Tài khoản của quý khách không đủ");
                }
            }
        });
    }

    private void paymment(View view) {
        TransactionMoney transaction = new TransactionMoney();
        transaction.transaction_type = "payment";
        transaction.wallet = DataLocalManager.getUid();
        transaction.supporter = DataLocalManager.getUid();
        transaction.description = "Movie Store:" + key_series.get();
        transaction.amount = seriesfilms.get().price;
        transaction.time = new SimpleDateFormat("HH:mm:ss").format(Utils.getRealtime());
        transaction.date = new SimpleDateFormat("dd/MM/yyyy").format(Utils.getRealtime());
        transaction.status = true;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Transaction").push().setValue(transaction, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(view.getContext(), "thanh toán thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), DetailSeriesFilmsVM.class);
                    intent.putExtra("key_series", key_series.get());
                    view.getContext().startActivity(intent);
                } else {
                    Toast.makeText(view.getContext(), "Vui lòng thử lại" + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onclicklike(View view) {
        String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Feedback feedback = new Feedback();
        feedback.key_user = DataLocalManager.getUid();
        feedback.time = time;
        feedback.comment = ":2107";
        feedback.status = true;
        feedback.key_film = key_epsode.get();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Feedback").push().setValue(feedback, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    countComment();
                } else {
                    Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onclickdislike(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback").child(key_feedback.get());
        databaseReference.removeValue();
        countComment();
    }

    public void onclickfeedback(@NonNull View view) {
        ArrayList<Feedback> list = new ArrayList<>();

        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogFeedbackBinding binding = CustomDialogFeedbackBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationFlyUp;
        window.setGravity(Gravity.BOTTOM);
        // load recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(binding.recyclerview.getContext(), RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(layoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback.key_film.equals(key_epsode.get())) {
                        if (feedback.comment.equals(":2107")) {

                        } else {
                            list.add(feedback);
                        }

                    }
                }
                FeedbackAdapter adapter = new FeedbackAdapter(list, view.getContext());
                binding.recyclerview.setHasFixedSize(true);
                binding.recyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.buttonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edittextsend.getText().toString().length() > 0) {
                    String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
                    Feedback feedback = new Feedback();
                    feedback.key_user = DataLocalManager.getUid();
                    feedback.time = time;
                    feedback.comment = binding.edittextsend.getText().toString();
                    feedback.status = true;
                    feedback.key_film = key_epsode.get();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Feedback").push().setValue(feedback, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                binding.edittextsend.setText("");
                            } else {
                                Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void checkNew() {
        isNew.set(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tham chiếu "Online Films" tồn tại, tiếp tục với logic của bạn
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "Check New: " + dataSnapshot.getKey());
                        if (dataSnapshot.getKey().equals(key_epsode.get())) {
                            isNew.set(true);
                        }
                    }
                    // ...
                } else {
                    // Tham chiếu "Online Films" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void checkOnline() {
        isOnline.set(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Online Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tham chiếu "Hot Films" tồn tại, tiếp tục với logic của bạn
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "Check Online: " + dataSnapshot.getKey());
                        if (dataSnapshot.getKey().equals(key_epsode.get())) {
                            isOnline.set(true);

                            price_epsode.set(Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
                            Log.d(TAG, "Price Online: " + price_epsode.get());
                        }
                    }
                    // ...
                } else {
                    // Tham chiếu "Hot Films" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void checkHot() {
        isHot.set(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Hot Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tham chiếu "Hot Films" tồn tại, tiếp tục với logic của bạn
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "Check Hot: " + dataSnapshot.getKey());
                        if (dataSnapshot.getKey().equals(key_epsode.get())) {
                            isHot.set(true);
                        }
                    }
                    // ...
                } else {
                    // Tham chiếu "Hot Films" không tồn tại
                    // Xử lý tình huống này theo ý muốn của bạn
                    // ...
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void countComment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int like = 0;
                int comment = 0;
                feeling.set(false);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback.key_film.equals(key_epsode.get()) && feedback.status) {
                        if (feedback.comment.equals(":2107")) {
                            like++;
                            if (feedback.key_user.equals(DataLocalManager.getUid())) {
                                key_feedback.set(dataSnapshot.getKey());
                                feeling.set(true);
                            }
                        } else {
                            comment++;
                        }
                    }
                }
                quantityComment.set("" + comment);
                quantityLike.set("" + like);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkBoughtEpsode (){
        bought_epsode.set(false);

        if (!admin.get()){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie Store").child(DataLocalManager.getUid()).child(key_epsode.get());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Tham chiếu "Movie Store" tồn tại
                        // ...
                        bought_epsode.set(true);
                    } else {
                        // Tham chiếu "Movie Store" không tồn tại
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
    private void checkBoughtSeries (){
        bought_series.set(false);

        if (!admin.get()){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie Store").child(DataLocalManager.getUid()).child(key_series.get());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Tham chiếu "Movie Store" tồn tại
                        // ...
                        bought_series.set(true);

                    } else {
                        // Tham chiếu "Movie Store" không tồn tại
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

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.ic_video_library_24);
        } else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}