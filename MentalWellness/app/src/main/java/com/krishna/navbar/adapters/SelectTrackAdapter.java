package com.krishna.navbar.adapters;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTrackAdapter extends RecyclerView.Adapter<SelectTrackAdapter.SelectTrackViewHolder> {

    private final Context context;
    private final List<Track> tracks;
    private final Map<Integer, Boolean> selectedItems;

    public SelectTrackAdapter(Context context, List<Track> tracks) {
        this.context = context;
        this.tracks = tracks;
        this.selectedItems = new HashMap<>();
        // Initialize all as unselected
        for (int i = 0; i < tracks.size(); i++) {
            selectedItems.put(i, false);
        }
    }

    @NonNull
    @Override
    public SelectTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_track, parent, false);
        return new SelectTrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectTrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.tvTitle.setText(track.getTitle());
        holder.tvArtist.setText(track.getArtist());
        holder.tvDuration.setText(track.getDuration());
        
        // Set checkbox state without triggering listener
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedItems.get(position));
        
        // Set up click listeners
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.put(position, isChecked);
        });
        
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !selectedItems.get(position);
            selectedItems.put(position, newState);
            holder.checkBox.setChecked(newState);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
    
    public void selectAllTracks() {
        for (int i = 0; i < tracks.size(); i++) {
            selectedItems.put(i, true);
        }
        notifyDataSetChanged();
    }
    
    public void clearSelection() {
        for (int i = 0; i < tracks.size(); i++) {
            selectedItems.put(i, false);
        }
        notifyDataSetChanged();
    }
    
    public List<Track> getSelectedTracks() {
        List<Track> selectedTracks = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            if (selectedItems.get(i)) {
                selectedTracks.add(tracks.get(i));
            }
        }
        return selectedTracks;
    }

    static class SelectTrackViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvTitle, tvArtist, tvDuration;

        public SelectTrackViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTrack);
            tvTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvArtist = itemView.findViewById(R.id.tvTrackArtist);
            tvDuration = itemView.findViewById(R.id.tvTrackDuration);
        }
    }
} 