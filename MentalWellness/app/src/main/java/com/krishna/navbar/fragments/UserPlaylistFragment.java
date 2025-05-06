package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.krishna.navbar.R;
import com.krishna.navbar.adapters.SongAdapter;
import com.krishna.navbar.models.Song;
import com.krishna.navbar.models.UserPlaylist;
import com.krishna.navbar.utils.FirebasePlaylistManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the contents of a user playlist from Firebase
 */
public class UserPlaylistFragment extends Fragment implements SongAdapter.OnSongClickListener {
    private static final String TAG = "UserPlaylistFragment";
    
    private String playlistId;
    private UserPlaylist currentPlaylist;
    private FirebasePlaylistManager playlistManager;
    
    // UI components
    private TextView tvPlaylistTitle;
    private TextView tvPlaylistTracks;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private View playlistBackground;
    private FloatingActionButton fabPlayAll;
    private ImageButton btnBack;
    private View emptyStateView;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get playlist ID from arguments
        if (getArguments() != null) {
            playlistId = getArguments().getString("playlistId");
        }
        
        // Initialize the playlist manager
        playlistManager = FirebasePlaylistManager.getInstance();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_playlist, container, false);
        
        // Initialize UI components
        tvPlaylistTitle = view.findViewById(R.id.tvUserPlaylistTitle);
        tvPlaylistTracks = view.findViewById(R.id.tvUserPlaylistTracks);
        recyclerView = view.findViewById(R.id.rvUserPlaylistSongs);
        playlistBackground = view.findViewById(R.id.clUserPlaylistHeader);
        fabPlayAll = view.findViewById(R.id.fabPlayAll);
        btnBack = view.findViewById(R.id.btnBack);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Set up click listeners
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        fabPlayAll.setOnClickListener(v -> playFirstSong());
        
        // Load playlist data
        loadPlaylist();
        
        // Set up real-time listener for playlist updates
        setupPlaylistListener();
        
        return view;
    }
    
    private void loadPlaylist() {
        if (playlistId == null || playlistId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid playlist ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get specific playlist document by ID and add a snapshot listener
        playlistManager.getUserPlaylistsCollection()
            .document(playlistId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // Convert document to playlist
                    DocumentSnapshot document = task.getResult();
                    currentPlaylist = playlistManager.convertDocumentToPlaylist(document);
                    
                    if (currentPlaylist != null) {
                        updateUI();
                    } else {
                        Toast.makeText(getContext(), "Error loading playlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Playlist not found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading playlist", task.getException());
                }
            });
    }
    
    private void setupPlaylistListener() {
        if (playlistId == null || playlistId.isEmpty()) {
            return;
        }
        
        // Add real-time listener to the playlist document
        playlistManager.getUserPlaylistsCollection()
            .document(playlistId)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "Error listening for playlist updates", error);
                        return;
                    }
                    
                    if (snapshot != null && snapshot.exists()) {
                        currentPlaylist = playlistManager.convertDocumentToPlaylist(snapshot);
                        if (currentPlaylist != null) {
                            updateUI();
                        }
                    }
                }
            });
    }
    
    private void updateUI() {
        if (currentPlaylist == null) {
            return;
        }
        
        // Set playlist details in UI
        tvPlaylistTitle.setText(currentPlaylist.getName());
        
        List<Song> songs = currentPlaylist.getSongs();
        int trackCount = songs != null ? songs.size() : 0;
        tvPlaylistTracks.setText(trackCount + " Tracks");
        
        // Set background color based on playlist theme
        playlistBackground.setBackgroundResource(currentPlaylist.getBackgroundResourceId());
        
        // Tint play button to match theme
        fabPlayAll.setColorFilter(getResources().getColor(currentPlaylist.getColorTint()));
        
        // Update song list in adapter
        adapter.updateSongs(songs != null ? songs : new ArrayList<>());
        
        // Show/hide empty state
        if (trackCount == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }
    
    private void playFirstSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs() == null || currentPlaylist.getSongs().isEmpty()) {
            Toast.makeText(getContext(), "No songs to play", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Start playing the first song
        navigateToPlayer(0);
    }
    
    @Override
    public void onSongClick(int position) {
        // Navigate to the player with the selected song
        navigateToPlayer(position);
    }
    
    private void navigateToPlayer(int trackIndex) {
        if (currentPlaylist == null) {
            return;
        }
        
        FirebasePlayerFragment playerFragment = new FirebasePlayerFragment();
        Bundle args = new Bundle();
        args.putString("playlistId", playlistId);
        args.putInt("trackIndex", trackIndex);
        playerFragment.setArguments(args);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }
} 