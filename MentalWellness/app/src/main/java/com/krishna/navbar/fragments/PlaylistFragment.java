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
        View headerBanner = view.findViewById(R.id.headerBanner);
        
        tvPlaylistTitle.setText(playlistName);
        
        // Set subtitle and background based on playlist
        switch (playlistName) {
            case "Finding Calm":
                tvPlaylistSubtitle.setText("Soothing melodies to bring peace to your mind");
                headerBanner.setBackgroundResource(R.drawable.gradient_blue_background);
                break;
            case "Spiritual":
                tvPlaylistSubtitle.setText("Connect with your inner self through these spiritual tracks");
                headerBanner.setBackgroundResource(R.drawable.gradient_yellow_background);
                break;
            case "Motivation":
                tvPlaylistSubtitle.setText("Empowering melodies to uplift and energize");
                headerBanner.setBackgroundResource(R.drawable.gradient_mint_background);
                break;
            case "Breathe":
                tvPlaylistSubtitle.setText("Gentle sounds to guide your breath and relax your body");
                headerBanner.setBackgroundResource(R.drawable.gradient_pink_background);
                break;
            case "Mindfulness":
                tvPlaylistSubtitle.setText("Be present and cultivate awareness with these mindful tracks");
                headerBanner.setBackgroundResource(R.drawable.gradient_purple_background);
                break;
            case "Sleep Well":
                tvPlaylistSubtitle.setText("Drift into deep, restorative sleep with calming sounds");
                headerBanner.setBackgroundResource(R.drawable.gradient_blue_dark_background);
                break;
            case "Healing":
                tvPlaylistSubtitle.setText("Support your emotional and physical healing journey");
                headerBanner.setBackgroundResource(R.drawable.gradient_orange_background);
                break;
            case "Anxiety Relief":
                tvPlaylistSubtitle.setText("Calm your mind and ease anxiety with these soothing melodies");
                headerBanner.setBackgroundResource(R.drawable.gradient_green_background);
                break;
            case "Positive Energy":
                tvPlaylistSubtitle.setText("Boost your mood and fill yourself with positive energy");
                headerBanner.setBackgroundResource(R.drawable.gradient_red_background);
                break;
            case "Stress Relief":
                tvPlaylistSubtitle.setText("Release tension and find relief from daily stress");
                headerBanner.setBackgroundResource(R.drawable.gradient_teal_background);
                break;
            case "My Zen Space":
                tvPlaylistSubtitle.setText("A personal collection of tracks for your zen moments");
                headerBanner.setBackgroundResource(R.drawable.gradient_blue_background);
                break;
            case "Morning Energy":
                tvPlaylistSubtitle.setText("Energizing tunes to kickstart your day with positivity");
                headerBanner.setBackgroundResource(R.drawable.gradient_yellow_background);
                break;
            default:
                tvPlaylistSubtitle.setText("A collection of tracks to improve your mental wellness");
                headerBanner.setBackgroundResource(R.drawable.gradient_blue_background);
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
                tracks.add(new Track("Sunset Serenity", "Nature Sounds", "4:55"));
                tracks.add(new Track("Quiet Meadow", "Ambient Dreams", "5:40"));
                tracks.add(new Track("Beach Waves", "Deep Relax", "6:05"));
                tracks.add(new Track("Soft Wind", "Nature Sounds", "4:30"));
                tracks.add(new Track("Moonlight Sonata", "Ambient Dreams", "6:20"));
                tracks.add(new Track("Flowing River", "Deep Relax", "5:15"));
                tracks.add(new Track("Morning Dew", "Nature Sounds", "4:50"));
                tracks.add(new Track("Tranquil Lake", "Ambient Dreams", "5:25"));
                tracks.add(new Track("Gentle Breeze", "Deep Relax", "4:40"));
                tracks.add(new Track("Peaceful Night", "Nature Sounds", "6:30"));
                tracks.add(new Track("Quiet Moments", "Ambient Dreams", "5:05"));
                tracks.add(new Track("Serene Wilderness", "Deep Relax", "5:50"));
                break;
                
            case "Spiritual":
                tracks.add(new Track("Sacred Om", "Meditation Masters", "7:20"));
                tracks.add(new Track("Inner Temple", "Soul Harmony", "6:30"));
                tracks.add(new Track("Divine Energy", "Meditation Masters", "5:45"));
                tracks.add(new Track("Soul Journey", "Soul Harmony", "8:10"));
                tracks.add(new Track("Healing Mantra", "Spiritual Guides", "6:20"));
                tracks.add(new Track("Sacred Space", "Meditation Masters", "7:15"));
                tracks.add(new Track("Inner Peace", "Soul Harmony", "6:40"));
                tracks.add(new Track("Divine Connection", "Spiritual Guides", "8:05"));
                break;
                
            case "Breathe":
                tracks.add(new Track("Deep Breath", "Breath Works", "4:33"));
                tracks.add(new Track("4-7-8 Technique", "Breath Masters", "5:15"));
                tracks.add(new Track("Mindful Breathing", "Breath Works", "6:40"));
                tracks.add(new Track("Cleansing Breath", "Breath Masters", "4:20"));
                tracks.add(new Track("Stress Release", "Calm Mind", "5:50"));
                tracks.add(new Track("Box Breathing", "Breath Works", "4:45"));
                tracks.add(new Track("Counting Breath", "Breath Masters", "5:30"));
                tracks.add(new Track("Diaphragmatic Breathing", "Calm Mind", "6:10"));
                tracks.add(new Track("Relaxed Breath", "Breath Works", "4:55"));
                tracks.add(new Track("Breath Awareness", "Breath Masters", "5:20"));
                tracks.add(new Track("Calming Breath", "Calm Mind", "4:40"));
                tracks.add(new Track("Energizing Breath", "Breath Works", "5:05"));
                tracks.add(new Track("Evening Breath Ritual", "Breath Masters", "6:25"));
                tracks.add(new Track("Morning Breath Practice", "Calm Mind", "5:35"));
                tracks.add(new Track("Breath and Release", "Breath Works", "4:50"));
                break;
                
            case "Mindfulness":
                tracks.add(new Track("Present Moment", "Mindful Living", "6:15"));
                tracks.add(new Track("Focused Awareness", "Zen Masters", "5:40"));
                tracks.add(new Track("Body Scan Meditation", "Mindful Living", "7:25"));
                tracks.add(new Track("Thought Observation", "Zen Masters", "6:10"));
                tracks.add(new Track("Mindful Walking", "Inner Peace", "5:50"));
                tracks.add(new Track("Sensory Awareness", "Mindful Living", "4:55"));
                tracks.add(new Track("Mindful Eating", "Zen Masters", "6:30"));
                tracks.add(new Track("Non-judgmental Awareness", "Inner Peace", "7:05"));
                tracks.add(new Track("Loving-Kindness", "Mindful Living", "8:20"));
                tracks.add(new Track("Mindful Movement", "Zen Masters", "5:45"));
                tracks.add(new Track("Open Awareness", "Inner Peace", "6:40"));
                tracks.add(new Track("Heart-Centered Mindfulness", "Mindful Living", "7:15"));
                break;
                
            case "Sleep Well":
                tracks.add(new Track("Dream Journey", "Sleep Soundly", "8:10"));
                tracks.add(new Track("Night Calm", "Restful Sleep", "7:45"));
                tracks.add(new Track("Deep Slumber", "Sleep Soundly", "9:20"));
                tracks.add(new Track("Bedtime Story", "Dreamy Night", "6:35"));
                tracks.add(new Track("Peaceful Night", "Restful Sleep", "8:55"));
                tracks.add(new Track("Sleep Deeply", "Sleep Soundly", "7:40"));
                tracks.add(new Track("Tranquil Dreams", "Dreamy Night", "8:15"));
                tracks.add(new Track("Nighttime Serenity", "Restful Sleep", "9:05"));
                tracks.add(new Track("Sweet Dreams", "Sleep Soundly", "7:50"));
                tracks.add(new Track("Drifting Off", "Dreamy Night", "6:45"));
                tracks.add(new Track("Sleep Sanctuary", "Restful Sleep", "8:30"));
                tracks.add(new Track("Moonlight Serenade", "Sleep Soundly", "7:25"));
                tracks.add(new Track("Starry Night", "Dreamy Night", "8:40"));
                tracks.add(new Track("Gentle Lullaby", "Restful Sleep", "6:55"));
                break;
                
            case "Healing":
                tracks.add(new Track("Inner Healing", "Healing Journey", "7:15"));
                tracks.add(new Track("Restoration", "Wellness Sounds", "6:40"));
                tracks.add(new Track("Self-Compassion", "Healing Journey", "8:05"));
                tracks.add(new Track("Recovery Meditation", "Wellness Sounds", "7:30"));
                tracks.add(new Track("Emotional Release", "Soul Medicine", "6:55"));
                tracks.add(new Track("Heart Healing", "Healing Journey", "8:25"));
                tracks.add(new Track("Cellular Renewal", "Wellness Sounds", "7:50"));
                tracks.add(new Track("Energy Cleansing", "Soul Medicine", "6:35"));
                tracks.add(new Track("Gentle Restoration", "Healing Journey", "7:45"));
                break;
                
            case "Anxiety Relief":
                tracks.add(new Track("Calm Mind", "Anxiety Free", "6:20"));
                tracks.add(new Track("Worry Release", "Peaceful Mind", "5:45"));
                tracks.add(new Track("Tranquil Thoughts", "Anxiety Free", "7:10"));
                tracks.add(new Track("Emotional Balance", "Peaceful Mind", "6:35"));
                tracks.add(new Track("Anxiety Melting", "Mental Clarity", "5:55"));
                tracks.add(new Track("Stress Dissolving", "Anxiety Free", "6:40"));
                tracks.add(new Track("Peace Within", "Peaceful Mind", "7:25"));
                tracks.add(new Track("Letting Go", "Mental Clarity", "6:15"));
                tracks.add(new Track("Centered Awareness", "Anxiety Free", "5:50"));
                tracks.add(new Track("Quiet Mind", "Peaceful Mind", "6:30"));
                break;
                
            case "Positive Energy":
                tracks.add(new Track("Radiant Energy", "Positivity", "4:50"));
                tracks.add(new Track("Joyful Heart", "Happy Mind", "5:15"));
                tracks.add(new Track("Uplift Your Spirit", "Positivity", "4:40"));
                tracks.add(new Track("Glowing Energy", "Happy Mind", "5:30"));
                tracks.add(new Track("Good Vibrations", "Light Energy", "4:55"));
                tracks.add(new Track("Abundant Joy", "Positivity", "5:25"));
                tracks.add(new Track("Bright Outlook", "Happy Mind", "4:45"));
                tracks.add(new Track("Inner Smile", "Light Energy", "5:10"));
                tracks.add(new Track("Happiness Flow", "Positivity", "5:35"));
                tracks.add(new Track("Cheerful Heart", "Happy Mind", "4:30"));
                tracks.add(new Track("Positive Mindset", "Light Energy", "5:20"));
                tracks.add(new Track("Optimism Boost", "Positivity", "4:35"));
                tracks.add(new Track("Gratitude Practice", "Happy Mind", "5:05"));
                break;
                
            case "Stress Relief":
                tracks.add(new Track("Release Tension", "Stress Free", "6:10"));
                tracks.add(new Track("Calming Waters", "Relaxation", "5:45"));
                tracks.add(new Track("Peaceful Mind", "Stress Free", "6:35"));
                tracks.add(new Track("Let It Go", "Relaxation", "5:25"));
                tracks.add(new Track("Unwinding", "Mind Reset", "6:50"));
                tracks.add(new Track("Mental Relief", "Stress Free", "5:55"));
                tracks.add(new Track("Deep Relaxation", "Relaxation", "6:20"));
                tracks.add(new Track("Stress Melting", "Mind Reset", "5:40"));
                tracks.add(new Track("Clear Mind", "Stress Free", "6:15"));
                tracks.add(new Track("Relax & Restore", "Relaxation", "5:50"));
                tracks.add(new Track("Gentle Relief", "Mind Reset", "6:05"));
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