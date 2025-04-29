package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.TrackAdapter;
import com.krishna.navbar.models.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private String playlistName;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_playlist, container, false);
        
        // Get playlist name from arguments
        if (getArguments() != null) {
            playlistName = getArguments().getString("playlistName", "Playlist");
        }
        
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        // Set playlist title and subtitle
        TextView tvPlaylistTitle = view.findViewById(R.id.tvPlaylistTitle);
        TextView tvPlaylistSubtitle = view.findViewById(R.id.tvPlaylistSubtitle);
        
        tvPlaylistTitle.setText(playlistName);
        
        // Set subtitle based on playlist
        switch (playlistName) {
            case "Finding Calm":
                tvPlaylistSubtitle.setText("Soothing melodies to bring peace to your mind");
                break;
            case "Spiritual":
                tvPlaylistSubtitle.setText("Connect with your inner self through these spiritual tracks");
                break;
            case "Motivation":
                tvPlaylistSubtitle.setText("Empowering melodies to uplift and energize");
                break;
            case "Breathe":
                tvPlaylistSubtitle.setText("Gentle sounds to guide your breath and relax your body");
                break;
            case "My Zen Space":
                tvPlaylistSubtitle.setText("A personal collection of tracks for your zen moments");
                break;
            case "Morning Energy":
                tvPlaylistSubtitle.setText("Energizing tunes to kickstart your day with positivity");
                break;
            default:
                tvPlaylistSubtitle.setText("A collection of tracks to improve your mental wellness");
                break;
        }
        
        // Set up play all button
        Button btnPlayAll = view.findViewById(R.id.btnPlayAll);
        btnPlayAll.setOnClickListener(v -> playAllTracks());
        
        // Set up recycler view with tracks
        RecyclerView rvTracks = view.findViewById(R.id.rvTracks);
        rvTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Create adapter with track list
        TrackAdapter adapter = new TrackAdapter(getTrackList(), trackPosition -> {
            // Play selected track
            playTrack(trackPosition);
        });
        
        rvTracks.setAdapter(adapter);
    }
    
    private List<Track> getTrackList() {
        // Create a list of tracks based on the playlist name
        List<Track> tracks = new ArrayList<>();
        
        switch (playlistName) {
            case "Motivation":
                tracks.add(new Track("Rise and Conquer", "InspiroTunes", "3:45"));
                tracks.add(new Track("Limitless Energy", "MindBoost", "4:02"));
                tracks.add(new Track("Fearless Spirit", "Peak Soundscapes", "3:30"));
                tracks.add(new Track("Positive Mindset", "InspiroTunes", "4:15"));
                tracks.add(new Track("Breakthrough", "MindBoost", "3:22"));
                tracks.add(new Track("Inner Power", "Peak Soundscapes", "3:50"));
                tracks.add(new Track("Unstoppable", "InspiroTunes", "4:10"));
                tracks.add(new Track("Reach Your Goals", "MindBoost", "3:38"));
                tracks.add(new Track("Clarity & Focus", "Peak Soundscapes", "4:05"));
                tracks.add(new Track("New Horizons", "InspiroTunes", "3:55"));
                tracks.add(new Track("Victory", "MindBoost", "3:15"));
                break;
                
            case "Finding Calm":
                tracks.add(new Track("Ocean Waves", "Nature Sounds", "5:10"));
                tracks.add(new Track("Gentle Rain", "Ambient Dreams", "4:45"));
                tracks.add(new Track("Mountain Stream", "Nature Sounds", "4:20"));
                tracks.add(new Track("Peaceful Forest", "Ambient Dreams", "5:30"));
                tracks.add(new Track("Evening Calm", "Deep Relax", "6:15"));
                // Add more tracks...
                break;
                
            case "Spiritual":
                tracks.add(new Track("Sacred Om", "Meditation Masters", "7:20"));
                tracks.add(new Track("Inner Temple", "Soul Harmony", "6:30"));
                tracks.add(new Track("Divine Energy", "Meditation Masters", "5:45"));
                tracks.add(new Track("Soul Journey", "Soul Harmony", "8:10"));
                tracks.add(new Track("Healing Mantra", "Spiritual Guides", "6:20"));
                // Add more tracks...
                break;
                
            case "Breathe":
                tracks.add(new Track("Deep Breath", "Breath Works", "4:33"));
                tracks.add(new Track("4-7-8 Technique", "Breath Masters", "5:15"));
                tracks.add(new Track("Mindful Breathing", "Breath Works", "6:40"));
                tracks.add(new Track("Cleansing Breath", "Breath Masters", "4:20"));
                tracks.add(new Track("Stress Release", "Calm Mind", "5:50"));
                // Add more tracks...
                break;
                
            default:
                // Default tracks for any other playlist
                tracks.add(new Track("Peaceful Melody", "Wellness Sounds", "3:45"));
                tracks.add(new Track("Calm Waters", "Nature Healing", "4:20"));
                tracks.add(new Track("Soothing Rain", "Ambient Sounds", "3:55"));
                tracks.add(new Track("Gentle Breeze", "Nature Healing", "4:10"));
                tracks.add(new Track("Mindful Moment", "Wellness Sounds", "3:30"));
                break;
        }
        
        return tracks;
    }
    
    private void playAllTracks() {
        // Start playing all tracks from the beginning
        playTrack(0);
    }
    
    private void playTrack(int position) {
        // Get the track information
        Track track = getTrackList().get(position);
        
        // Start playing the track
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("trackName", track.getTitle());
        args.putString("artistName", track.getArtist());
        args.putString("playlistName", playlistName);
        args.putInt("trackIndex", position);
        playerFragment.setArguments(args);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }
} 