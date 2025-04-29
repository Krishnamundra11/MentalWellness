package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;
import com.krishna.navbar.dialogs.SelectTracksDialog;
import com.krishna.navbar.models.Track;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        
        // Example button to show track selection dialog
        view.findViewById(R.id.btnSelectTracks).setOnClickListener(v -> showTrackSelectionDialog());
        
        return view;
    }
    
    private void showTrackSelectionDialog() {
        // Sample tracks for demo purposes
        List<Track> tracks = getSampleTracks();
        
        SelectTracksDialog dialog = new SelectTracksDialog(
                requireContext(),
                tracks,
                "Select Music Tracks",
                selectedTracks -> {
                    // Handle selected tracks
                    StringBuilder builder = new StringBuilder();
                    builder.append("Selected ").append(selectedTracks.size()).append(" tracks: ");
                    
                    for (int i = 0; i < selectedTracks.size(); i++) {
                        if (i > 0) builder.append(", ");
                        builder.append(selectedTracks.get(i).getTitle());
                        // Limit the display to first 3 tracks
                        if (i == 2 && selectedTracks.size() > 3) {
                            builder.append(", and ").append(selectedTracks.size() - 3).append(" more");
                            break;
                        }
                    }
                    
                    Toast.makeText(requireContext(), builder.toString(), Toast.LENGTH_LONG).show();
                });
                
        dialog.show();
    }
    
    private List<Track> getSampleTracks() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("Peaceful Mind", "Nature Sounds", "3:45"));
        tracks.add(new Track("Deep Meditation", "Zen Master", "5:20"));
        tracks.add(new Track("Sleep Well", "Dream Makers", "8:15"));
        tracks.add(new Track("Morning Calm", "Sunrise Beats", "4:10"));
        tracks.add(new Track("Stress Relief", "Healing Tones", "6:30"));
        tracks.add(new Track("Focus Time", "Study Music", "10:00"));
        tracks.add(new Track("Yoga Flow", "Body & Mind", "7:25"));
        tracks.add(new Track("Ocean Waves", "Nature Sounds", "5:45"));
        tracks.add(new Track("Forest Ambience", "Planet Earth", "9:30"));
        tracks.add(new Track("Rain Sounds", "Natural Elements", "8:00"));
        return tracks;
    }
} 