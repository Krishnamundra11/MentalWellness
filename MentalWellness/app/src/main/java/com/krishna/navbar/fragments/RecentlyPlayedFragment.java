package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.TrackAdapter;
import com.krishna.navbar.models.Track;
import com.krishna.navbar.utils.PlaylistManager;

import java.util.ArrayList;
import java.util.List;

public class RecentlyPlayedFragment extends Fragment {
    private static final String TAG = "RecentlyPlayedFragment";

    private RecyclerView rvRecentTracks;
    private TextView tvEmptyState;
    private List<Track> recentTracks = new ArrayList<>();
    private TrackAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_played, container, false);
        setupUI(view);
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshContent();
    }

    private void setupUI(View view) {
        rvRecentTracks = view.findViewById(R.id.rvRecentTracks);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rvRecentTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize the adapter with empty list
        adapter = new TrackAdapter(recentTracks, position -> {
            if (position >= 0 && position < recentTracks.size()) {
                Track track = recentTracks.get(position);
                playTrack(track.getTitle(), track.getArtist());
            }
        });
        
        rvRecentTracks.setAdapter(adapter);
        
        // Load the content
        refreshContent();
    }
    
    /**
     * Public method to refresh content
     */
    public void refreshContent() {
        try {
            Log.d(TAG, "Refreshing recently played tracks");
            
            // Get recently played tracks
            List<Track> freshTracks = getRecentlyPlayedTracks();
            
            // Update the list
            recentTracks.clear();
            recentTracks.addAll(freshTracks);
            
            // Update UI
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            
            updateEmptyState();
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing content", e);
        }
    }
    
    private void updateEmptyState() {
        if (recentTracks.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvRecentTracks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvRecentTracks.setVisibility(View.VISIBLE);
        }
    }
    
    private List<Track> getRecentlyPlayedTracks() {
        // In a real app, this would come from a local database or shared preferences
        // For this example, we'll create a mock list
        List<Track> recentTracks = new ArrayList<>();
        
        // Get tracks from motivational playlist as "recently played"
        List<Track> motivationTracks = PlaylistManager.getInstance().getPlaylistTracks("Motivation");
        recentTracks.addAll(motivationTracks);
        
        // Add a couple more sample tracks
        recentTracks.add(new Track("Night Meditation", "Zen Masters", "8:35"));
        recentTracks.add(new Track("Morning Light", "Nature Awakens", "4:20"));
        
        return recentTracks;
    }
    
    private void playTrack(String trackName, String artistName) {
        // Navigate to player fragment
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("trackName", trackName);
        args.putString("artistName", artistName);
        playerFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }
} 