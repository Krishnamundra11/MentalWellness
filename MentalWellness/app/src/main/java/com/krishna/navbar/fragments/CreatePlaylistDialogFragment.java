package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krishna.navbar.R;
import com.krishna.navbar.models.UserPlaylist;

/**
 * DialogFragment for creating a new playlist
 */
public class CreatePlaylistDialogFragment extends DialogFragment {
    
    private OnPlaylistCreatedListener listener;
    
    // Different color options for playlists
    private static final String COLOR_BLUE = "blue";
    private static final String COLOR_YELLOW = "yellow";
    private static final String COLOR_PURPLE = "purple";
    private static final String COLOR_GREEN = "green";
    private static final String COLOR_RED = "red";
    private static final String COLOR_TEAL = "teal";
    
    public interface OnPlaylistCreatedListener {
        void onPlaylistCreated(UserPlaylist playlist);
    }
    
    public void setOnPlaylistCreatedListener(OnPlaylistCreatedListener listener) {
        this.listener = listener;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_playlist, container, false);
        
        // Set dialog width to match parent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        
        TextInputEditText editTextPlaylistName = view.findViewById(R.id.editTextPlaylistName);
        RadioGroup radioGroupColors = view.findViewById(R.id.radioGroupColors);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnCreate = view.findViewById(R.id.btnCreate);
        
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnCreate.setOnClickListener(v -> {
            String playlistName = editTextPlaylistName.getText().toString().trim();
            if (playlistName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a playlist name", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if user is authenticated
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(getContext(), "You need to sign in to create playlists", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            
            // Get selected color
            int checkedId = radioGroupColors.getCheckedRadioButtonId();
            String selectedColor = COLOR_BLUE; // Default
            
            if (checkedId == R.id.radioBlue) {
                selectedColor = COLOR_BLUE;
            } else if (checkedId == R.id.radioYellow) {
                selectedColor = COLOR_YELLOW;
            } else if (checkedId == R.id.radioPurple) {
                selectedColor = COLOR_PURPLE;
            } else if (checkedId == R.id.radioGreen) {
                selectedColor = COLOR_GREEN;
            } else if (checkedId == R.id.radioRed) {
                selectedColor = COLOR_RED;
            } else if (checkedId == R.id.radioTeal) {
                selectedColor = COLOR_TEAL;
            }
            
            // Create the new playlist with empty songs list
            UserPlaylist newPlaylist = new UserPlaylist(playlistName, selectedColor);
            
            // Notify listener that a playlist was created
            if (listener != null) {
                listener.onPlaylistCreated(newPlaylist);
            }
            
            dismiss();
        });
        
        return view;
    }
} 