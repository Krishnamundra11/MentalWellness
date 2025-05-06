package com.krishna.navbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.models.Song;

import java.util.List;

/**
 * Adapter for displaying songs in a RecyclerView
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    
    private List<Song> songs;
    private OnSongClickListener listener;
    
    public interface OnSongClickListener {
        void onSongClick(int position);
    }
    
    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }
    
    public void updateSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view, listener);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, position);
    }
    
    @Override
    public int getItemCount() {
        return songs.size();
    }
    
    static class SongViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private TextView tvSongNumber;
        
        public SongViewHolder(@NonNull View itemView, OnSongClickListener listener) {
            super(itemView);
            
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvSongNumber = itemView.findViewById(R.id.tvSongNumber);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onSongClick(getAdapterPosition());
                }
            });
        }
        
        public void bind(Song song, int position) {
            tvSongTitle.setText(song.getSongTitle());
            tvArtistName.setText(song.getArtistName());
            tvSongNumber.setText(String.valueOf(position + 1));
        }
    }
} 