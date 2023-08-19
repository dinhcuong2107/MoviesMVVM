package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DetailFilmsVM extends ViewModel {
    public ObservableField<String> key_film = new ObservableField<>();
    public ObservableField<String> key_feedback = new ObservableField<>();
    public ObservableField<String> quantityLike = new ObservableField<>();
    public ObservableField<String> quantityComment = new ObservableField<>();

    public ObservableField<Boolean> admin = new ObservableField<>();
    public ObservableField<Boolean> feeling = new ObservableField<>();
    public ObservableField<Boolean> isNew = new ObservableField<>();
    public ObservableField<Boolean> isHot = new ObservableField<>();
    public ObservableField<Boolean> isOnline = new ObservableField<>();

    public ObservableField<Films> films = new ObservableField<>();
    public ObservableField<String> onlinePrice = new ObservableField<>();

    public DetailFilmsVM(String key) {
        key_film.set(key);
        admin.set(DataLocalManager.getAdmin());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(key_film.get());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                films.set(snapshot.getValue(Films.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        countComment();
        checkOnline();
        checkNew();
        checkHot();
    }

    private void checkOnline() {
        isOnline.set(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Online Films");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tham chiếu "Online Films" tồn tại, tiếp tục với logic của bạn
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "Check Online: " + dataSnapshot.getKey());
                        if (dataSnapshot.getKey().equals(key_film.get())) {
                            isOnline.set(true);
                            onlinePrice.set(Utils.convertPriceToVND(Integer.parseInt(dataSnapshot.getValue().toString())));
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
                        if (dataSnapshot.getKey().equals(key_film.get())) {
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
                        if (dataSnapshot.getKey().equals(key_film.get())) {
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
                    if (feedback.key_film.equals(key_film.get()) && feedback.status) {
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


    public void onclicktrailer(View view) {
        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
        intent.putExtra("video", films.get().videoTrailer);
        intent.putExtra("name", films.get().name);
        view.getContext().startActivity(intent);
    }

    public void onclickvideo(View view) {
        if (admin.get()){
            Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
            intent.putExtra("video", films.get().video);
            intent.putExtra("name", films.get().name);
            view.getContext().startActivity(intent);
        }else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie Store" + "/" + DataLocalManager.getUid() + "/" + key_film.get());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Tham chiếu "Movie Store" tồn tại, tiếp tục với logic của bạn
                        Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
                        intent.putExtra("video", films.get().video);
                        intent.putExtra("name", films.get().name);
                        view.getContext().startActivity(intent);

                        // ...
                    } else {
                        // Tham chiếu "Movie Store" không tồn tại
                        // Xử lý tình huống này theo ý muốn của bạn
                        // ...
                        openDialogMovieStore(view);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

    public void onclickbuy(View view) {
        Intent intent = new Intent(view.getContext(), ShowTimesActivity.class);
        intent.putExtra("key_film", key_film);
        view.getContext().startActivity(intent);
    }

    public void onShowPopupMenu(View view) {
        Button button = view.findViewById(R.id.button);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), button);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_films, popupMenu.getMenu());

        MenuItem item_online = popupMenu.getMenu().findItem(R.id.item_online);
        MenuItem item_delete_online = popupMenu.getMenu().findItem(R.id.item_online_delete);
        MenuItem item_new = popupMenu.getMenu().findItem(R.id.item_new);
        MenuItem item_delete_new = popupMenu.getMenu().findItem(R.id.item_new_delete);
        MenuItem item_hot = popupMenu.getMenu().findItem(R.id.item_hot);
        MenuItem item_delete_hot = popupMenu.getMenu().findItem(R.id.item_hot_delete);
        MenuItem item_status_true = popupMenu.getMenu().findItem(R.id.item_status_true);
        MenuItem item_status_false = popupMenu.getMenu().findItem(R.id.item_status_false);

        if (films.get().status) {
            item_status_false.setVisible(true);
            item_status_true.setVisible(false);


            if (isHot.get()) {
                item_hot.setVisible(false);
                item_delete_hot.setVisible(true);
            } else {
                item_hot.setVisible(true);
                item_delete_hot.setVisible(false);
            }

            if (isNew.get()) {
                item_new.setVisible(false);
                item_delete_new.setVisible(true);
            } else {
                item_new.setVisible(true);
                item_delete_new.setVisible(false);
            }

            if (isOnline.get()) {
                item_online.setVisible(false);
                item_delete_online.setVisible(true);
            } else {
                item_online.setVisible(true);
                item_delete_online.setVisible(false);
            }

        } else {
            item_status_false.setVisible(false);
            item_status_true.setVisible(true);

            item_status_false.setVisible(false);
            item_online.setVisible(false);
            item_delete_online.setVisible(false);
            item_hot.setVisible(false);
            item_delete_hot.setVisible(false);
            item_new.setVisible(false);
            item_delete_new.setVisible(false);

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_online:
                        openDialogSetOnlineFilms(view);
                        return true;
                    case R.id.item_online_delete:
                        openDialogDeleteOnlineFilms(view);
                        return true;
                    case R.id.item_hot:
                        openDialogSetHotFilms(view);
                        return true;
                    case R.id.item_hot_delete:
                        openDialogDeleteHotFilms(view);
                        return true;
                    case R.id.item_new:
                        openDialogSetNewFilms(view);
                        return true;
                    case R.id.item_new_delete:
                        openDialogDeleteNewFilms(view);
                        return true;
                    case R.id.item_status_true:
                        openDialogSetStatusTrue(view);
                        return true;
                    case R.id.item_status_false:
                        openDialogSetStatusFalse(view);
                        return true;
                    case R.id.item_update:
                        Intent intent = new Intent(view.getContext(), AddFilmsActivity.class);
                        intent.putExtra("key_film", key_film.get());
                        view.getContext().startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openDialogSetStatusTrue(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn công chiếu");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Films/" + key_film.get() + "/status");
                // SetValue
                dataRef.setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(view.getContext(), "Phìm đã công chiếu", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Complete");
                            reloadIntent(view);
                        } else {
                            Log.d(TAG, "Error" + error);
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogSetStatusFalse(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn ngừng công chiếu");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Films/" + key_film.get() + "/status");

                // SetValue
                dataRef.setValue(false, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(view.getContext(), "Phìm đã ngừng công chiếu", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Complete");
                            reloadIntent(view);
                        } else {
                            Log.d(TAG, "Error" + error);
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogMovieStore(View view) {

        Utils.getBalanceFromDatabase(DataLocalManager.getUid(),new Utils.BalanceListener() {
            @Override
            public void onAmountCalculated(int amount) {
                // Xử lý số liệu 'amount'
                Log.d("Total Amount", String.valueOf(amount));
                if (amount >= Utils.convertVNDToPrice(onlinePrice.get()))
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

                    questionBinding.textview.setText("Bạn muốn thêm phim '"+ films.get().name +"' vào Store với giá " + onlinePrice.get());
                    questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());

                    questionBinding.push.setText("Thanh toán và xem ngay");
                    questionBinding.push.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Movie Store").child(DataLocalManager.getUid()).child(key_film.get()).setValue(true, new DatabaseReference.CompletionListener() {
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
        transaction.amount = Utils.convertVNDToPrice(onlinePrice.get());
        transaction.wallet = DataLocalManager.getUid();
        transaction.supporter = DataLocalManager.getUid();
        transaction.description = "Movie Store:" + key_film.get();
        transaction.time = new SimpleDateFormat("HH:mm:ss").format(Utils.getRealtime());
        transaction.date = new SimpleDateFormat("dd/MM/yyyy").format(Utils.getRealtime());
        transaction.status = true;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Transaction").push().setValue(transaction, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(view.getContext(), "thanh toán thành công", Toast.LENGTH_SHORT).show();
                    Utils.reLoadIntent(view.getContext());
                } else {
                    Toast.makeText(view.getContext(), "Vui lòng thử lại" + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openDialogSetNewFilms(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn thêm vào phim mới");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("New Films").child(key_film.get()).setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            reloadIntent(v);
                            Toast.makeText(v.getContext(), "Đã thêm vào danh sách phim mới ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogDeleteNewFilms(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn xóa khỏi dạnh sách phim mới");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("New Films/" + key_film.get());

                // Xóa giá trị
                dataRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(view.getContext(), "Phìm đã xóa khỏi danh sách phim mới", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Delete: " + "Complete");
                            reloadIntent(view);
                        } else {
                            Log.d(TAG, "Delete: " + "Error" + error);
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogSetHotFilms(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn thêm vào phim nổi bật");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Hot Films").child(key_film.get()).setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            reloadIntent(v);
                            Toast.makeText(v.getContext(), "Đã thêm vào danh sách phim nổi bật ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogDeleteHotFilms(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn xóa khỏi dạnh sách phim nổi bật");
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Hot Films/" + key_film.get());

                // Xóa giá trị
                dataRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(view.getContext(), "Phìm đã xóa khỏi danh sách phim nổi bật", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Delete: " + "Complete");
                            reloadIntent(view);
                        } else {
                            Log.d(TAG, "Delete: " + "Error" + error);
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }
    private void openDialogSetOnlineFilms(View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogOnlineFilmsBinding binding = CustomDialogOnlineFilmsBinding.inflate(LayoutInflater.from(view.getContext()));
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.TOP);
        dialog.setCancelable(false);

        Picasso.get().load(films.get().poster).into(binding.filmsPoster);
        binding.filmsName.setText(films.get().name);
        binding.filmsGenre.setText(films.get().genre);
        binding.filmsDirector.setText(films.get().director);
        binding.filmsActors.setText(films.get().main_actors);
        binding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.price.getText().length() > 0) {
                    Dialog dialogquestion = new Dialog(view.getContext());
                    dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
                    dialogquestion.setContentView(questionBinding.getRoot());

                    Window window = dialogquestion.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
                    window.setGravity(Gravity.CENTER);
                    dialogquestion.setCancelable(false);

                    questionBinding.textview.setText("Bạn muốn mở chiếu phim: " + films.get().name + "\n với giá: " + Utils.convertPriceToVND(Integer.valueOf(binding.price.getText().toString())));
                    questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
                    questionBinding.push.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Online Films").child(key_film.get()).setValue(Integer.valueOf(binding.price.getText().toString()), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        reloadIntent(v);
                                        Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    dialogquestion.show();
                } else {
                    Toast.makeText(v.getContext(), "Bổ sung giá", Toast.LENGTH_SHORT).show();
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
    private void openDialogDeleteOnlineFilms(View view) {
        Dialog dialogquestion = new Dialog(view.getContext());
        dialogquestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomDialogQuestionBinding questionBinding = CustomDialogQuestionBinding.inflate(LayoutInflater.from(view.getContext()));
        dialogquestion.setContentView(questionBinding.getRoot());

        Window window = dialogquestion.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.getAttributes().windowAnimations = R.style.DialogAnimationDrop;
        window.setGravity(Gravity.CENTER);
        dialogquestion.setCancelable(false);

        questionBinding.textview.setText("Bạn muốn hủy công chiếu online phim: " + films.get().name);
        questionBinding.cancel.setOnClickListener(v1 -> dialogquestion.dismiss());
        questionBinding.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi tạo và cấu hình kết nối với Firebase
                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Online Films/" + key_film.get());

                // Xóa giá trị
                dataRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(view.getContext(), "Phìm đã xóa khỏi danh sách phim online", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Delete Online: " + "Complete");
                            reloadIntent(view);
                        } else {
                            Log.d(TAG, "Delete Online: " + "Error" + error);
                        }
                    }
                });
            }
        });
        dialogquestion.show();
    }

    private void reloadIntent(View v) {
        Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(v.getContext(), DetailFilmsActivity.class);
        intent.putExtra("key_film", key_film.get());
        v.getContext().startActivity(intent);
    }

    public void onclicklike(View view) {
        String time = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Feedback feedback = new Feedback();
        feedback.key_user = DataLocalManager.getUid();
        feedback.time = time;
        feedback.comment = ":2107";
        feedback.status = true;
        feedback.key_film = key_film.get();

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
                    if (feedback.key_film.equals(key_film.get())) {
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
                    feedback.key_film = key_film.get();

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

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl) {
        if (imgUrl == null) {
            imageView.setImageResource(R.drawable.ic_video_library_24);
        } else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}