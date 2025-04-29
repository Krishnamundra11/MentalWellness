package com.krishna.navbar.fragments;

import android.os.Bundle;
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

    private RecyclerView rvRecentTracks;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_played, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        rvRecentTracks = view.findViewById(R.id.rvRecentTracks);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rvRecentTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get recently played tracks
        List<Track> recentTracks = getRecentlyPlayedTracks();
        
        if (recentTracks.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvRecentTracks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvRecentTracks.setVisibility(View.VISIBLE);
            
            TrackAdapter adapter = new TrackAdapter(recentTracks, position -> {
                Track track = recentTracks.get(position);
                playTrack(track.getTitle(), track.getArtist());
            });
            
            rvRecentTracks.setAdapter(adapter);
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