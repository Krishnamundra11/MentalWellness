package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;

public class MusicMainFragment extends Fragment {

    private View mainContent; // Main content containing all playlists
    private boolean isFilterActive = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_main, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        // Reference to main content area (all playlists)
        mainContent = view.findViewById(R.id.mainContent);
        
        // Set up library button
        ImageButton btnLibrary = view.findViewById(R.id.btnLibrary);
        btnLibrary.setOnClickListener(v -> openLibrary());
        
        // Set up category chip listeners
        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipRelax = view.findViewById(R.id.chipRelax);
        Chip chipBalance = view.findViewById(R.id.chipBalance);
        Chip chipMind = view.findViewById(R.id.chipMind);

        chipAll.setOnClickListener(v -> {
            // "All" button should reset to show all content
            if (isFilterActive) {
                showAllPlaylists();
            }
        });
        
        chipRelax.setOnClickListener(v -> filterCategory("Relax"));
        chipBalance.setOnClickListener(v -> filterCategory("Balance"));
        chipMind.setOnClickListener(v -> filterCategory("Mind"));

        // Setup card click listeners
        View cardCalmPlaylist = view.findViewById(R.id.cardCalmPlaylist);
        View cardSpiritualPlaylist = view.findViewById(R.id.cardSpiritualPlaylist);
        View cardMotivationPlaylist = view.findViewById(R.id.cardMotivationPlaylist);
        View cardBreathePlaylist = view.findViewById(R.id.cardBreathePlaylist);

        cardCalmPlaylist.setOnClickListener(v -> openPlaylist("Finding Calm"));
        cardSpiritualPlaylist.setOnClickListener(v -> openPlaylist("Spiritual"));
        cardMotivationPlaylist.setOnClickListener(v -> openPlaylist("Motivation"));
        cardBreathePlaylist.setOnClickListener(v -> openPlaylist("Breathe"));

        // Setup play button listeners
        FloatingActionButton fabCalmPlay = view.findViewById(R.id.fabCalmPlay);
        FloatingActionButton fabSpiritualPlay = view.findViewById(R.id.fabSpiritualPlay);
        FloatingActionButton fabMotivationPlay = view.findViewById(R.id.fabMotivationPlay);
        FloatingActionButton fabBreathePlay = view.findViewById(R.id.fabBreathePlay);

        fabCalmPlay.setOnClickListener(v -> playPlaylist("Finding Calm"));
        fabSpiritualPlay.setOnClickListener(v -> playPlaylist("Spiritual"));
        fabMotivationPlay.setOnClickListener(v -> playPlaylist("Motivation"));
        fabBreathePlay.setOnClickListener(v -> playPlaylist("Breathe"));
    }

    private void filterCategory(String category) {
        isFilterActive = true;
        
        // Open a playlist based on the selected category
        switch (category) {
            case "Relax":
                openPlaylist("Finding Calm");
                break;
            case "Balance":
                openPlaylist("Spiritual");
                break;
            case "Mind":
                openPlaylist("Motivation");
                break;
        }
    }
    
    private void showAllPlaylists() {
        // Reset filter and show all content
        isFilterActive = false;
        
        // No navigation, just ensure all content is visible
        if (mainContent != null) {
            mainContent.setVisibility(View.VISIBLE);
        }
    }

    private void openLibrary() {
        // Open the library fragment showing all playlists
        LibraryFragment libraryFragment = new LibraryFragment();
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, libraryFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openPlaylist(String playlistName) {
        // Open playlist fragment with list of tracks
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString("playlistName", playlistName);
        playlistFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playlistFragment)
                .addToBackStack(null)
                .commit();
    }

    private void playPlaylist(String playlistName) {
        // Start playing the first track in the playlist
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("playlistName", playlistName);
        args.putInt("trackIndex", 0);
        playerFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }
} 