package com.krishna.navbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for selecting tracks when creating a playlist
 */
public class SelectTracksAdapter extends RecyclerView.Adapter<SelectTracksAdapter.SelectTrackViewHolder> {

    public interface OnTrackSelectListener {
        void onTrackSelected(Track track);
    }

    private final List<Track> tracks;
    private final List<Track> selectedTracks = new ArrayList<>();
    private final OnTrackSelectListener listener;

    public SelectTracksAdapter(List<Track> tracks, OnTrackSelectListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_track, parent, false);
        return new SelectTrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectTrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.bind(track, selectedTracks.contains(track));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    class SelectTrackViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTrackTitle;
        private final TextView tvTrackArtist;
        private final TextView tvTrackDuration;
        private final CheckBox cbSelectTrack;

        public SelectTrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvTrackArtist = itemView.findViewById(R.id.tvTrackArtist);
            tvTrackDuration = itemView.findViewById(R.id.tvTrackDuration);
            cbSelectTrack = itemView.findViewById(R.id.checkBoxTrack);
        }

        public void bind(Track track, boolean isSelected) {
            tvTrackTitle.setText(track.getTitle());
            tvTrackArtist.setText(track.getArtist());
            tvTrackDuration.setText(track.getDuration());
            cbSelectTrack.setChecked(isSelected);
            
            // Set click listener for the whole item
            itemView.setOnClickListener(v -> {
                boolean newState = !cbSelectTrack.isChecked();
                cbSelectTrack.setChecked(newState);
                
                if (newState) {
                    selectedTracks.add(track);
                } else {
                    selectedTracks.remove(track);
                }
                
                listener.onTrackSelected(track);
            });
            
            // Set click listener for the checkbox
            cbSelectTrack.setOnClickListener(v -> {
                boolean isChecked = cbSelectTrack.isChecked();
                
                if (isChecked) {
                    selectedTracks.add(track);
                } else {
                    selectedTracks.remove(track);
                }
                
                listener.onTrackSelected(track);
            });
        }
    }
} 