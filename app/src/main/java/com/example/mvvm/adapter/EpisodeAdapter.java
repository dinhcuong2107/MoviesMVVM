package com.example.mvvm.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mvvm.R;
import com.example.mvvm.Utils;
import com.example.mvvm.databinding.ItemEpisodeBinding;
import com.example.mvvm.databinding.ItemFastfoodBinding;
import com.example.mvvm.datalocal.DataLocalManager;
import com.example.mvvm.function.AddSeriesFilmsVM;
import com.example.mvvm.function.DetailSeriesFilmsVM;
import com.example.mvvm.function.VideoPlayerActivity;
import com.example.mvvm.model.Films;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {
    List<String> list;
    Boolean show;
    Films films;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public EpisodeAdapter(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<String> getListSelect() {
        return list;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEpisodeBinding binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodeAdapter.EpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        if (show){
            holder.binding.text.setVisibility(View.VISIBLE);
            holder.binding.delete.setVisibility(View.VISIBLE);
            // cho phép di chuyển nội dung hiển thị trên TextView
            holder.binding.text.setSelected(true);
        }else {
            holder.binding.text.setVisibility(View.GONE);
            holder.binding.delete.setVisibility(View.GONE);
        }
        holder.binding.textEpisode.setText("" + (position + 1));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Films").child(list.get(position));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                films = snapshot.getValue(Films.class);
                holder.binding.text.setText(""+films.name);

                if (!films.status){
                    holder.binding.layoutEpisode.setBoxBackgroundColor(Color.parseColor("#FF4433"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.textEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (!show){
                        clickListener.onItemClick(position);
                    }

                }
            }
        });
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void setEpisodeAdapter(List<String> list, Boolean show) {
        this.list = list;
        this.show = show;
        notifyDataSetChanged();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private ItemEpisodeBinding binding;
        public EpisodeViewHolder(@NonNull ItemEpisodeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}