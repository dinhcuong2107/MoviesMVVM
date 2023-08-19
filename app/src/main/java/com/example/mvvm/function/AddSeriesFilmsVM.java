package com.example.mvvm.function;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.MainActivity;
import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.adapter.FeedbackAdapter;
import com.example.mvvm.databinding.CustomDialogFeedbackBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.livedata.FilmsOnlineLiveData;
import com.example.mvvm.model.Feedback;
import com.example.mvvm.model.Films;
import com.example.mvvm.model.SeriesFilms;
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
import java.util.UUID;

public class AddSeriesFilmsVM extends ViewModel {

    private MutableLiveData<List<String>> liveData;

    public MutableLiveData<List<String>> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<List<String>>();

            liveData.setValue(new ArrayList<>());

        }
        episode.set(liveData.getValue().size()+1);
        return liveData;
    }

    public void setLiveData(MutableLiveData<List<String>> liveData) {
        this.liveData = liveData;
    }

    public ObservableField<Films> films = new ObservableField<>();
    public ObservableField<String> select = new ObservableField<>();
    public ObservableField<String> key_series = new ObservableField<>();
    public ObservableField<Integer> episode = new ObservableField<>();
    public ObservableField<String> series_price = new ObservableField<>();
    public ObservableField<String> series_name = new ObservableField<>();
    public ObservableField<Integer> series_totalEpisode = new ObservableField<>();
    public ObservableField<Boolean> series_status = new ObservableField<>();

    public ObservableField<String> key_film = new ObservableField<>();
    public ObservableField<Boolean> update = new ObservableField<>();

    public ObservableField<String> error = new ObservableField<>();

    public AddSeriesFilmsVM() {
        if (episode.get() == null){
            episode.set(1);
        }else {
        }
    }

    public void setSeries(String s){
        key_series.set(s);
        update.set(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Series Films").child(s);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SeriesFilms seriesFilms = snapshot.getValue(SeriesFilms.class);

                liveData.setValue(Utils.convertStringToList(seriesFilms.epsodecode));

                series_name.set(seriesFilms.name);
                series_totalEpisode.set(seriesFilms.totalEpisode);
                series_price.set(seriesFilms.price.toString());
                series_status.set(seriesFilms.status);

                load_data_film();
                episode.set(liveData.getValue().size()+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setSelect(String s){
        select.set(s);
    }
    public void delete_epsode(int i) {
        List list = liveData.getValue();

        list.remove(i);

        liveData.setValue(list);

        episode.set(liveData.getValue().size()+1);
    }
    public void click_status(View view){
        if (series_status.get() == null || series_status.get()== false){
            series_status.set(true);

        }else {
            series_status.set(false);
        }
    }
    public void click_seclect (View view) {
        if (series_totalEpisode.get() != null && series_totalEpisode.get() >= episode.get())
        {
            if (select.get() != null && select.get().length() > 0){
                if (Utils.checkForExistence(select.get(),liveData.getValue())) {
                    Toast.makeText(view.getContext(), "Đã có trong danh sách", Toast.LENGTH_LONG).show();
                } else {
                    List<String> list = liveData.getValue();
                    list.add(select.get());
                    liveData.setValue(list);
                    episode.set(liveData.getValue().size()+1);
                    select.set("");

                    load_data_film();
                }

            } else {
                Toast.makeText(view.getContext(), "Chọn phim cần thêm!", Toast.LENGTH_LONG).show();
            }
        } else {
            Utils.showError(view.getContext(), "Vui lòng kiểm tra lại tổng số tập phim đã thiết lập");
        }
    }
    public void click_add (View view){
        if (check_data()){
            SeriesFilms seriesFilms = new SeriesFilms(series_name.get(), series_totalEpisode.get(), Integer.parseInt(series_price.get()),  liveData.getValue().toString(),series_status.get());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Series Films").push().setValue(seriesFilms, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(view.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(view.getContext(), error.get().toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void click_update (View view){
        if (check_data()){
            SeriesFilms seriesFilms = new SeriesFilms(series_name.get(), series_totalEpisode.get(), Integer.parseInt(series_price.get()),  liveData.getValue().toString(),series_status.get());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Series Films").child(key_series.get()).setValue(seriesFilms, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(view.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(view.getContext(), error.get().toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void click_increase (View view){
        if (series_totalEpisode.get() == null){
            series_totalEpisode.set(1);
        }else {
            series_totalEpisode.set(series_totalEpisode.get()+1);
        }
    }
    public void click_decrease (View view){
        if (series_totalEpisode.get() == null || series_totalEpisode.get() == 0){
            series_totalEpisode.set(0);
        }else {
            if (liveData.getValue() != null)
            {
                if (series_totalEpisode.get() > liveData.getValue().size())
                {
                    series_totalEpisode.set(series_totalEpisode.get()-1);
                } else {
                    Toast.makeText(view.getContext(), "Không thể thiêt lập ( <" + liveData.getValue().size() + ")", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void load_data_film() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(liveData.getValue().get(0));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                films.set(snapshot.getValue(Films.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean check_data() {
        if (series_name.get() == null || series_name.get().trim().length() == 0)
        {
            error.set("Kiểm tra tên phim");
            return false;
        }
        if (series_totalEpisode.get() == null)
        {
            error.set("Kiểm tra tổng tập phim");
            return false;
        }
        if (liveData.getValue().size() == 0)
        {
            error.set("Kiểm tra danh sách tập phim");
            return false;
        }
        if (series_price.get() == null)
        {
            error.set("Kiểm tra giá công chiếu");
            return false;
        }
        return true;
    }

    @BindingAdapter({"android:src"})
    public static void setImageView(ImageView imageView, String imgUrl){
        if (imgUrl==null){
            imageView.setImageResource(R.drawable.ic_video_library_24);
        }
        else {
            Picasso.get().load(imgUrl).into(imageView);
        }
    }
}