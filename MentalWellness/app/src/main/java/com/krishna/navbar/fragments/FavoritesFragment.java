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
import com.krishna.navbar.utils.FavoritesManager;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavoriteTracks;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        rvFavoriteTracks = view.findViewById(R.id.rvFavoriteTracks);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rvFavoriteTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get favorite tracks
        List<Track> favoriteTracks = FavoritesManager.getInstance().getFavoriteTracks();
        
        if (favoriteTracks.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvFavoriteTracks.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvFavoriteTracks.setVisibility(View.VISIBLE);
            
            TrackAdapter adapter = new TrackAdapter(favoriteTracks, position -> {
                Track track = favoriteTracks.get(position);
                playTrack(track.getTitle(), track.getArtist());
            });
            
            rvFavoriteTracks.setAdapter(adapter);
        }
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