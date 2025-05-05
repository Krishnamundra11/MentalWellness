package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;
import com.krishna.navbar.models.UserPlaylist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicMainFragment extends Fragment implements CreatePlaylistDialogFragment.OnPlaylistCreatedListener {

    private boolean isFilterActive = false;
    private String currentCategory = "All";
    
    // Container views for each category
    private ConstraintLayout allPlaylistsContainer;
    private ConstraintLayout relaxPlaylistsContainer;
    private ConstraintLayout balancePlaylistsContainer;
    private ConstraintLayout mindPlaylistsContainer;
    private ConstraintLayout myPlaylistCategory;
    
    // Empty state views for My Playlist category
    private TextView tvEmptyPlaylist;
    private LinearLayout userPlaylistsContainer;
    
    // List to store user-created playlists
    private List<UserPlaylist> userPlaylists = new ArrayList<>();
    
    // Map to store standardized playlist names
    private static final Map<String, String> playlistNameMap = new HashMap<>();
    
    // Initialize playlist name mapping
    static {
        // All canonical names for each playlist
        playlistNameMap.put("Finding Calm", "Finding Calm");
        playlistNameMap.put("Spiritual", "Spiritual");
        playlistNameMap.put("Motivation", "Motivation");
        playlistNameMap.put("Breathe", "Breathe");
        playlistNameMap.put("Mindfulness", "Mindfulness");
        playlistNameMap.put("Sleep Well", "Sleep Well");
        playlistNameMap.put("Healing", "Healing");
        playlistNameMap.put("Anxiety Relief", "Anxiety Relief");
        playlistNameMap.put("Positive Energy", "Positive Energy");
        playlistNameMap.put("Stress Relief", "Stress Relief");
    }
    
    // Helper method to ensure consistent playlist names
    private String getStandardPlaylistName(String playlistName) {
        return playlistNameMap.getOrDefault(playlistName, playlistName);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_main, container, false);
        setupUI(view);
        setupBackButtonHandling();
        return view;
    }

    private void setupBackButtonHandling() {
        // Handle back button press when filter is active
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), 
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (isFilterActive && !currentCategory.equals("All")) {
                        // If filter is active, revert to showing all playlists
                        showAllPlaylists();
                        
                        // Reset the chip selection to "All"
                        Chip chipAll = getView().findViewById(R.id.chipAll);
                        chipAll.setChecked(true);
                    } else {
                        // Otherwise, handle normally by removing this callback
                        this.setEnabled(false);
                        requireActivity().onBackPressed();
                    }
                }
            });
    }

    private void setupUI(View view) {
        // Initialize container views for each category
        allPlaylistsContainer = view.findViewById(R.id.mainContent);
        relaxPlaylistsContainer = view.findViewById(R.id.relaxCategory);
        balancePlaylistsContainer = view.findViewById(R.id.balanceCategory);
        mindPlaylistsContainer = view.findViewById(R.id.mindCategory);
        myPlaylistCategory = view.findViewById(R.id.myPlaylistCategory);
        
        // Initialize views for My Playlist category
        tvEmptyPlaylist = view.findViewById(R.id.tvEmptyPlaylist);
        userPlaylistsContainer = view.findViewById(R.id.userPlaylistsContainer);
        
        // Set up Create Playlist button
        ExtendedFloatingActionButton fabCreatePlaylist = view.findViewById(R.id.fabCreatePlaylist);
        fabCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
        
        // Set up library button
        ImageButton btnLibrary = view.findViewById(R.id.btnLibrary);
        btnLibrary.setOnClickListener(v -> openLibrary());
        
        // Set up category chip listeners
        Chip chipAll = view.findViewById(R.id.chipAll);
        Chip chipRelax = view.findViewById(R.id.chipRelax);
        Chip chipBalance = view.findViewById(R.id.chipBalance);
        Chip chipMind = view.findViewById(R.id.chipMind);
        Chip chipMyPlaylist = view.findViewById(R.id.chipMyPlaylist);

        chipAll.setOnClickListener(v -> {
            currentCategory = "All";
            showAllPlaylists();
        });
        
        chipRelax.setOnClickListener(v -> {
            currentCategory = "Relax";
            filterCategory("Relax");
        });
        
        chipBalance.setOnClickListener(v -> {
            currentCategory = "Balance";
            filterCategory("Balance");
        });
        
        chipMind.setOnClickListener(v -> {
            currentCategory = "Mind";
            filterCategory("Mind");
        });
        
        chipMyPlaylist.setOnClickListener(v -> {
            currentCategory = "MyPlaylist";
            filterCategory("MyPlaylist");
        });

        // Setup click listeners for original playlists in All category
        setupAllCategoryListeners(view);
        
        // Setup click listeners for Relax category
        setupRelaxCategoryListeners(view);
        
        // Setup click listeners for Balance category
        setupBalanceCategoryListeners(view);
        
        // Setup click listeners for Mind category
        setupMindCategoryListeners(view);
        
        // Show all playlists by default
        showAllPlaylists();
        
        // Update My Playlist UI state
        updateMyPlaylistUI();
    }
    
    private void setupAllCategoryListeners(View view) {
        // Original playlists - All category
        View cardCalmPlaylist = view.findViewById(R.id.cardCalmPlaylist);
        View cardSpiritualPlaylist = view.findViewById(R.id.cardSpiritualPlaylist);
        View cardMotivationPlaylist = view.findViewById(R.id.cardMotivationPlaylist);
        View cardBreathePlaylist = view.findViewById(R.id.cardBreathePlaylist);
        View cardMindfulnessPlaylist = view.findViewById(R.id.cardMindfulnessPlaylist);
        View cardSleepWellPlaylist = view.findViewById(R.id.cardSleepWellPlaylist);
        View cardHealingPlaylist = view.findViewById(R.id.cardHealingPlaylist);
        View cardAnxietyReliefPlaylist = view.findViewById(R.id.cardAnxietyReliefPlaylist);
        View cardPositiveEnergyPlaylist = view.findViewById(R.id.cardPositiveEnergyPlaylist);
        View cardStressReliefPlaylist = view.findViewById(R.id.cardStressReliefPlaylist);
        
        // Set click listeners for all cards
        cardCalmPlaylist.setOnClickListener(v -> openPlaylist("Finding Calm"));
        cardSpiritualPlaylist.setOnClickListener(v -> openPlaylist("Spiritual"));
        cardMotivationPlaylist.setOnClickListener(v -> openPlaylist("Motivation"));
        cardBreathePlaylist.setOnClickListener(v -> openPlaylist("Breathe"));
        cardMindfulnessPlaylist.setOnClickListener(v -> openPlaylist("Mindfulness"));
        cardSleepWellPlaylist.setOnClickListener(v -> openPlaylist("Sleep Well"));
        cardHealingPlaylist.setOnClickListener(v -> openPlaylist("Healing"));
        cardAnxietyReliefPlaylist.setOnClickListener(v -> openPlaylist("Anxiety Relief"));
        cardPositiveEnergyPlaylist.setOnClickListener(v -> openPlaylist("Positive Energy"));
        cardStressReliefPlaylist.setOnClickListener(v -> openPlaylist("Stress Relief"));
        
        // Set up play button listeners
        setupPlayButtonListeners(view);
    }
    
    private void setupRelaxCategoryListeners(View view) {
        // Relax category playlist cards
        View cardCalmRelax = view.findViewById(R.id.cardCalmRelax);
        View cardBreatheRelax = view.findViewById(R.id.cardBreatheRelax);
        View cardSleepWellRelax = view.findViewById(R.id.cardSleepWellRelax);
        View cardStressReliefRelax = view.findViewById(R.id.cardStressReliefRelax);
        
        // Set click listeners for Relax category cards
        cardCalmRelax.setOnClickListener(v -> openPlaylist("Finding Calm"));
        cardBreatheRelax.setOnClickListener(v -> openPlaylist("Breathe"));
        cardSleepWellRelax.setOnClickListener(v -> openPlaylist("Sleep Well"));
        cardStressReliefRelax.setOnClickListener(v -> openPlaylist("Stress Relief"));
        
        // Set up play button listeners for Relax category
        FloatingActionButton fabCalmRelaxPlay = view.findViewById(R.id.fabCalmRelaxPlay);
        FloatingActionButton fabBreatheRelaxPlay = view.findViewById(R.id.fabBreatheRelaxPlay);
        FloatingActionButton fabSleepWellRelaxPlay = view.findViewById(R.id.fabSleepWellRelaxPlay);
        FloatingActionButton fabStressReliefRelaxPlay = view.findViewById(R.id.fabStressReliefRelaxPlay);
        
        fabCalmRelaxPlay.setOnClickListener(v -> playPlaylist("Finding Calm"));
        fabBreatheRelaxPlay.setOnClickListener(v -> playPlaylist("Breathe"));
        fabSleepWellRelaxPlay.setOnClickListener(v -> playPlaylist("Sleep Well"));
        fabStressReliefRelaxPlay.setOnClickListener(v -> playPlaylist("Stress Relief"));
    }
    
    private void setupBalanceCategoryListeners(View view) {
        // Balance category playlist cards
        View cardSpiritualBalance = view.findViewById(R.id.cardSpiritualBalance);
        View cardMindfulnessBalance = view.findViewById(R.id.cardMindfulnessBalance);
        View cardHealingBalance = view.findViewById(R.id.cardHealingBalance);
        
        // Set click listeners for Balance category cards
        cardSpiritualBalance.setOnClickListener(v -> openPlaylist("Spiritual"));
        cardMindfulnessBalance.setOnClickListener(v -> openPlaylist("Mindfulness"));
        cardHealingBalance.setOnClickListener(v -> openPlaylist("Healing"));
        
        // Set up play button listeners for Balance category
        FloatingActionButton fabSpiritualBalancePlay = view.findViewById(R.id.fabSpiritualBalancePlay);
        FloatingActionButton fabMindfulnessBalancePlay = view.findViewById(R.id.fabMindfulnessBalancePlay);
        FloatingActionButton fabHealingBalancePlay = view.findViewById(R.id.fabHealingBalancePlay);
        
        fabSpiritualBalancePlay.setOnClickListener(v -> playPlaylist("Spiritual"));
        fabMindfulnessBalancePlay.setOnClickListener(v -> playPlaylist("Mindfulness"));
        fabHealingBalancePlay.setOnClickListener(v -> playPlaylist("Healing"));
    }
    
    private void setupMindCategoryListeners(View view) {
        // Mind category playlist cards
        View cardMotivationMind = view.findViewById(R.id.cardMotivationMind);
        View cardPositiveEnergyMind = view.findViewById(R.id.cardPositiveEnergyMind);
        View cardAnxietyReliefMind = view.findViewById(R.id.cardAnxietyReliefMind);
        
        // Set click listeners for Mind category cards
        cardMotivationMind.setOnClickListener(v -> openPlaylist("Motivation"));
        cardPositiveEnergyMind.setOnClickListener(v -> openPlaylist("Positive Energy"));
        cardAnxietyReliefMind.setOnClickListener(v -> openPlaylist("Anxiety Relief"));
        
        // Set up play button listeners for Mind category
        FloatingActionButton fabMotivationMindPlay = view.findViewById(R.id.fabMotivationMindPlay);
        FloatingActionButton fabPositiveEnergyMindPlay = view.findViewById(R.id.fabPositiveEnergyMindPlay);
        FloatingActionButton fabAnxietyReliefMindPlay = view.findViewById(R.id.fabAnxietyReliefMindPlay);
        
        fabMotivationMindPlay.setOnClickListener(v -> playPlaylist("Motivation"));
        fabPositiveEnergyMindPlay.setOnClickListener(v -> playPlaylist("Positive Energy"));
        fabAnxietyReliefMindPlay.setOnClickListener(v -> playPlaylist("Anxiety Relief"));
    }
    
    private void setupPlayButtonListeners(View view) {
        // Original playlists
        FloatingActionButton fabCalmPlay = view.findViewById(R.id.fabCalmPlay);
        FloatingActionButton fabSpiritualPlay = view.findViewById(R.id.fabSpiritualPlay);
        FloatingActionButton fabMotivationPlay = view.findViewById(R.id.fabMotivationPlay);
        FloatingActionButton fabBreathePlay = view.findViewById(R.id.fabBreathePlay);
        
        // New playlists
        FloatingActionButton fabMindfulnessPlay = view.findViewById(R.id.fabMindfulnessPlay);
        FloatingActionButton fabSleepWellPlay = view.findViewById(R.id.fabSleepWellPlay);
        FloatingActionButton fabHealingPlay = view.findViewById(R.id.fabHealingPlay);
        FloatingActionButton fabAnxietyReliefPlay = view.findViewById(R.id.fabAnxietyReliefPlay);
        FloatingActionButton fabPositiveEnergyPlay = view.findViewById(R.id.fabPositiveEnergyPlay);
        FloatingActionButton fabStressReliefPlay = view.findViewById(R.id.fabStressReliefPlay);
        
        // Set click listeners for play buttons
        fabCalmPlay.setOnClickListener(v -> playPlaylist("Finding Calm"));
        fabSpiritualPlay.setOnClickListener(v -> playPlaylist("Spiritual"));
        fabMotivationPlay.setOnClickListener(v -> playPlaylist("Motivation"));
        fabBreathePlay.setOnClickListener(v -> playPlaylist("Breathe"));
        fabMindfulnessPlay.setOnClickListener(v -> playPlaylist("Mindfulness"));
        fabSleepWellPlay.setOnClickListener(v -> playPlaylist("Sleep Well"));
        fabHealingPlay.setOnClickListener(v -> playPlaylist("Healing"));
        fabAnxietyReliefPlay.setOnClickListener(v -> playPlaylist("Anxiety Relief"));
        fabPositiveEnergyPlay.setOnClickListener(v -> playPlaylist("Positive Energy"));
        fabStressReliefPlay.setOnClickListener(v -> playPlaylist("Stress Relief"));
    }

    private void filterCategory(String category) {
        isFilterActive = true;
        
        // Hide all containers
        allPlaylistsContainer.setVisibility(View.GONE);
        relaxPlaylistsContainer.setVisibility(View.GONE);
        balancePlaylistsContainer.setVisibility(View.GONE);
        mindPlaylistsContainer.setVisibility(View.GONE);
        myPlaylistCategory.setVisibility(View.GONE);
        
        // Show only the selected category container
        switch (category) {
            case "Relax":
                relaxPlaylistsContainer.setVisibility(View.VISIBLE);
                break;
            case "Balance":
                balancePlaylistsContainer.setVisibility(View.VISIBLE);
                break;
            case "Mind":
                mindPlaylistsContainer.setVisibility(View.VISIBLE);
                break;
            case "MyPlaylist":
                myPlaylistCategory.setVisibility(View.VISIBLE);
                updateMyPlaylistUI();
                break;
        }
    }
    
    private void showAllPlaylists() {
        // Reset filter and show All container, hide category containers
        isFilterActive = false;
        currentCategory = "All";
        
        allPlaylistsContainer.setVisibility(View.VISIBLE);
        relaxPlaylistsContainer.setVisibility(View.GONE);
        balancePlaylistsContainer.setVisibility(View.GONE);
        mindPlaylistsContainer.setVisibility(View.GONE);
        myPlaylistCategory.setVisibility(View.GONE);
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
        args.putString("playlistName", getStandardPlaylistName(playlistName));
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
        args.putString("playlistName", getStandardPlaylistName(playlistName));
        args.putInt("trackIndex", 0);
        playerFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void showCreatePlaylistDialog() {
        CreatePlaylistDialogFragment dialogFragment = new CreatePlaylistDialogFragment();
        dialogFragment.setOnPlaylistCreatedListener(this);
        dialogFragment.show(getChildFragmentManager(), "CreatePlaylistDialog");
    }
    
    @Override
    public void onPlaylistCreated(UserPlaylist playlist) {
        // Add the new playlist to both the user playlist list and the standardization map
        userPlaylists.add(playlist);
        playlistNameMap.put(playlist.getName(), playlist.getName());
        
        // Update the UI to show the new playlist
        updateMyPlaylistUI();
        
        // If we're in All view, also add the playlist to the All category
        if (currentCategory.equals("All")) {
            addPlaylistToAllCategory(playlist);
        }
    }
    
    private void updateMyPlaylistUI() {
        // Show/hide empty state message based on whether there are playlists
        if (userPlaylists.isEmpty()) {
            tvEmptyPlaylist.setVisibility(View.VISIBLE);
            userPlaylistsContainer.setVisibility(View.GONE);
        } else {
            tvEmptyPlaylist.setVisibility(View.GONE);
            userPlaylistsContainer.setVisibility(View.VISIBLE);
            
            // Clear existing playlists and re-add all
            userPlaylistsContainer.removeAllViews();
            
            // Add each user playlist to the container
            for (UserPlaylist playlist : userPlaylists) {
                addPlaylistToContainer(playlist);
            }
        }
    }
    
    private void addPlaylistToContainer(UserPlaylist playlist) {
        // Inflate the user playlist item layout
        View playlistView = getLayoutInflater().inflate(R.layout.item_user_playlist, userPlaylistsContainer, false);
        
        // Set background based on color theme
        View background = playlistView.findViewById(R.id.playlistBackground);
        background.setBackgroundResource(playlist.getBackgroundResourceId());
        
        // Set playlist title and track count
        TextView tvTitle = playlistView.findViewById(R.id.tvUserPlaylistTitle);
        TextView tvTracks = playlistView.findViewById(R.id.tvUserPlaylistTracks);
        
        tvTitle.setText(playlist.getName());
        tvTracks.setText(playlist.getTrackCount() + " Tracks");
        
        // Set up play button with correct tint
        FloatingActionButton fabPlay = playlistView.findViewById(R.id.fabUserPlaylistPlay);
        fabPlay.setColorFilter(getResources().getColor(playlist.getColorTint()));
        
        // Set up click listeners
        playlistView.setOnClickListener(v -> openPlaylist(playlist.getName()));
        fabPlay.setOnClickListener(v -> playPlaylist(playlist.getName()));
        
        // Add the view to the container
        userPlaylistsContainer.addView(playlistView);
    }
    
    private void addPlaylistToAllCategory(UserPlaylist playlist) {
        // In a real app, this would dynamically add a new playlist card to the All category
        // For this demo, we'll just update the UI when switching categories
    }
} 