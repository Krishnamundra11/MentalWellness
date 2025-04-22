package com.example.mentalwellness.ui.meditation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;

import java.util.List;

public class RelatedMeditationAdapter extends RecyclerView.Adapter<RelatedMeditationAdapter.ViewHolder> {

    private final List<MeditationSession> meditationList;
    private final OnMeditationClickListener listener;

    public interface OnMeditationClickListener {
        void onMeditationClick(MeditationSession meditation);
    }

    public RelatedMeditationAdapter(List<MeditationSession> meditationList, OnMeditationClickListener listener) {
        this.meditationList = meditationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_related_meditation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MeditationSession meditation = meditationList.get(position);
        holder.tvTitle.setText(meditation.getTitle());
        holder.tvDuration.setText(meditation.getDuration());
        
        // In a real app, you would load the image from a URL or resource
        // using a library like Glide or Picasso
        holder.ivThumbnail.setImageResource(meditation.getImageResourceId());
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMeditationClick(meditation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meditationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ImageView ivThumbnail;
        final TextView tvTitle;
        final TextView tvDuration;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;  // The root view is already a CardView
            ivThumbnail = view.findViewById(R.id.meditation_image);
            tvTitle = view.findViewById(R.id.meditation_title);
            tvDuration = view.findViewById(R.id.meditation_duration);
        }
    }
} 