package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.SelectTrackAdapter;
import com.krishna.navbar.models.Track;
import com.krishna.navbar.utils.PlaylistManager;

import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment for creating a new playlist
 */
public class CreatePlaylistDialogFragment extends DialogFragment {

    private EditText etPlaylistName;
    private RecyclerView rvSelectTracks;
    private SelectTrackAdapter adapter;
    private Button btnSelectAll;
    private Button btnCancel;
    private Button btnCreate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_playlist, container, false);
        
        // Set dialog window style
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        setupUI(view);
        return view;
    }
    
    private void setupUI(View view) {
        // Initialize views
        etPlaylistName = view.findViewById(R.id.etPlaylistName);
        rvSelectTracks = view.findViewById(R.id.rvSelectTracks);
        btnSelectAll = view.findViewById(R.id.btnSelectAll);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);
        
        // Get all available tracks
        List<Track> allTracks = getAllAvailableTracks();
        
        // Setup RecyclerView
        rvSelectTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SelectTrackAdapter(requireContext(), allTracks);
        rvSelectTracks.setAdapter(adapter);
        
        // Setup button listeners
        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAllTracks();
            Toast.makeText(getContext(), "All tracks selected", Toast.LENGTH_SHORT).show();
        });
        
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnCreate.setOnClickListener(v -> {
            String playlistName = etPlaylistName.getText().toString().trim();
            List<Track> selectedTracks = adapter.getSelectedTracks();
            
            if (playlistName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a playlist name", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedTracks.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one track", Toast.LENGTH_SHORT).show();
                return;
            }
            
            createPlaylist(playlistName, selectedTracks);
        });
    }
    
    /**
     * Get all available tracks that can be added to a playlist
     */
    private List<Track> getAllAvailableTracks() {
        // In a real app, this would fetch tracks from a database
        // For this demo, we'll create a static list
        List<Track> tracks = new ArrayList<>();
        
        // Finding Calm playlist tracks
        tracks.add(new Track("Ocean Waves", "Nature Sounds", "5:10"));
        tracks.add(new Track("Gentle Rain", "Ambient Dreams", "4:45"));
        tracks.add(new Track("Mountain Stream", "Nature Sounds", "4:20"));
        tracks.add(new Track("Peaceful Forest", "Ambient Dreams", "5:30"));
        
        // Spiritual playlist tracks
        tracks.add(new Track("Sacred Om", "Meditation Masters", "7:20"));
        tracks.add(new Track("Inner Temple", "Soul Harmony", "6:30"));
        tracks.add(new Track("Divine Energy", "Meditation Masters", "5:45"));
        
        // Motivation playlist tracks
        tracks.add(new Track("Rise and Conquer", "InspiroTunes", "3:45"));
        tracks.add(new Track("Limitless Energy", "MindBoost", "4:02"));
        tracks.add(new Track("Fearless Spirit", "Peak Soundscapes", "3:30"));
        
        // Breathe playlist tracks
        tracks.add(new Track("Deep Breath", "Breath Works", "4:33"));
        tracks.add(new Track("4-7-8 Technique", "Breath Masters", "5:15"));
        tracks.add(new Track("Mindful Breathing", "Breath Works", "6:40"));
        
        return tracks;
    }
    
    /**
     * Create a new playlist with the given name and tracks
     */
    private void createPlaylist(String playlistName, List<Track> tracks) {
        // In a real app, this would save to a database
        // For this demo, we'll use our PlaylistManager
        boolean success = PlaylistManager.getInstance().createPlaylist(playlistName, tracks);
        
        if (success) {
            Toast.makeText(getContext(), "Playlist '" + playlistName + "' created with " + tracks.size() + " tracks", Toast.LENGTH_SHORT).show();
            
            // Navigate to the new playlist
            if (getActivity() != null) {
                PlaylistFragment playlistFragment = new PlaylistFragment();
                Bundle args = new Bundle();
                args.putString("playlistName", playlistName);
                playlistFragment.setArguments(args);
                
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.con, playlistFragment)
                        .addToBackStack(null)
                        .commit();
                
                dismiss();
            }
        } else {
            Toast.makeText(getContext(), "A playlist with this name already exists", Toast.LENGTH_SHORT).show();
        }
    }
} 