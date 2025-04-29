package com.krishna.navbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    public interface OnTrackClickListener {
        void onTrackClick(int position);
    }

    private final List<Track> tracks;
    private final OnTrackClickListener listener;

    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.tvTrackTitle.setText(track.getTitle());
        holder.tvTrackArtist.setText(track.getArtist());
        holder.tvTrackDuration.setText(track.getDuration());
        
        holder.fabPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(position);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackTitle;
        TextView tvTrackArtist;
        TextView tvTrackDuration;
        FloatingActionButton fabPlay;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvTrackArtist = itemView.findViewById(R.id.tvTrackArtist);
            tvTrackDuration = itemView.findViewById(R.id.tvTrackDuration);
            fabPlay = itemView.findViewById(R.id.fabTrackPlay);
        }
    }
} 