package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.R;
import com.krishna.navbar.models.UserPlaylist;
import com.krishna.navbar.utils.FirebasePlaylistManager;
import com.krishna.navbar.utils.UserUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicMainFragment extends Fragment implements CreatePlaylistDialogFragment.OnPlaylistCreatedListener {

    private static final String TAG = "MusicMainFragment";
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
    
    // Firebase components
    private FirebaseAuth firebaseAuth;
    private FirebasePlaylistManager playlistManager;
    
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
    
    private TextView tvGreeting;
    private UserUtils userUtils;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userUtils = UserUtils.getInstance();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_main, container, false);
        
        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        playlistManager = FirebasePlaylistManager.getInstance();
        
        // Initialize views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        setupUI(view);
        setupBackButtonHandling();
        
        // Load user playlists from Firebase
        loadUserPlaylistsFromFirebase();
        
        // Set up Firebase playlist listener for real-time updates
        setupFirebasePlaylistListener();
        
        // Update greeting with user's name
        updateGreeting();
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        // Load playlists from Firebase when fragment becomes visible
        loadUserPlaylistsFromFirebase();
        
        // Set up real-time listener for playlist updates
        setupFirebasePlaylistListener();
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
        fabCreatePlaylist.setOnClickListener(v -> {
            // Check if user is authenticated
            if (firebaseAuth.getCurrentUser() != null) {
                showCreatePlaylistDialog();
            } else {
                Toast.makeText(getContext(), "You need to sign in to create playlists", Toast.LENGTH_SHORT).show();
                // You could redirect to login here
            }
        });
        
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
        // Check if it's a user playlist
        UserPlaylist userPlaylist = findUserPlaylistByName(playlistName);
        
        if (userPlaylist != null) {
            // This is a user playlist, open it from Firestore
            openUserPlaylist(userPlaylist);
        } else {
            // This is a default playlist, use the legacy approach
            PlaylistFragment playlistFragment = new PlaylistFragment();
            Bundle args = new Bundle();
            args.putString("playlistName", getStandardPlaylistName(playlistName));
            playlistFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.con, playlistFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
    
    private void openUserPlaylist(UserPlaylist playlist) {
        // Create fragment to show user playlist
        UserPlaylistFragment userPlaylistFragment = new UserPlaylistFragment();
        Bundle args = new Bundle();
        args.putString("playlistId", playlist.getId());
        userPlaylistFragment.setArguments(args);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, userPlaylistFragment)
                .addToBackStack(null)
                .commit();
    }

    private void playPlaylist(String playlistName) {
        // Check if it's a user playlist
        UserPlaylist userPlaylist = findUserPlaylistByName(playlistName);
        
        if (userPlaylist != null && userPlaylist.getSongs() != null && !userPlaylist.getSongs().isEmpty()) {
            // This is a user playlist with songs, play the first song
            playUserPlaylistTrack(userPlaylist, 0);
        } else {
            // This is a default playlist or empty user playlist, use the legacy approach
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
    }
    
    private void playUserPlaylistTrack(UserPlaylist playlist, int trackIndex) {
        if (playlist.getSongs() == null || playlist.getSongs().isEmpty() || trackIndex >= playlist.getSongs().size()) {
            Toast.makeText(getContext(), "No tracks available in this playlist", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Start the PlayerFragment with the Firebase song
        FirebasePlayerFragment playerFragment = new FirebasePlayerFragment();
        Bundle args = new Bundle();
        args.putString("playlistId", playlist.getId());
        args.putInt("trackIndex", trackIndex);
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
        // Save the new playlist to Firebase
        playlistManager.savePlaylist(playlist, new FirebasePlaylistManager.OnPlaylistSavedListener() {
            @Override
            public void onPlaylistSaved(UserPlaylist savedPlaylist, Exception exception) {
                if (exception == null) {
                    // Saved successfully
                    Toast.makeText(getContext(), "Playlist '" + savedPlaylist.getName() + "' created", Toast.LENGTH_SHORT).show();
                    
                    // Add the playlist to our local list to ensure immediate UI update
                    userPlaylists.add(savedPlaylist);
                    playlistNameMap.put(savedPlaylist.getName(), savedPlaylist.getName());
                    
                    // Switch to My Playlist category to show the new playlist
                    currentCategory = "MyPlaylist";
                    filterCategory("MyPlaylist");
                    
                    // The Firebase listener will update the UI when the data is synced
                } else {
                    // Error saving
                    Toast.makeText(getContext(), "Error creating playlist: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error saving playlist", exception);
                }
            }
        });
    }
    
    private void loadUserPlaylistsFromFirebase() {
        // Check if user is authenticated
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "User not authenticated, skipping playlist load");
            return;
        }
        
        // Load playlists from Firestore
        playlistManager.getAllPlaylists(new FirebasePlaylistManager.OnPlaylistsLoadedListener() {
            @Override
            public void onPlaylistsLoaded(List<UserPlaylist> playlists, Exception exception) {
                if (exception == null) {
                    // Update the playlist list
                    userPlaylists.clear();
                    userPlaylists.addAll(playlists);
                    
                    // Add playlist names to the standardization map
                    for (UserPlaylist playlist : playlists) {
                        playlistNameMap.put(playlist.getName(), playlist.getName());
                    }
                    
                    // Update the UI
                    updateMyPlaylistUI();
                    
                    Log.d(TAG, "Loaded " + playlists.size() + " playlists from Firebase");
                } else {
                    // Error loading playlists
                    Log.e(TAG, "Error loading playlists from Firebase", exception);
                    Toast.makeText(getContext(), "Error loading playlists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void setupFirebasePlaylistListener() {
        // Check if user is authenticated
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "User not authenticated, skipping playlist listener setup");
            return;
        }
        
        // Set up real-time listener
        playlistManager.listenForPlaylistUpdates(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Error listening for playlist updates", error);
                    return;
                }
                
                if (value != null) {
                    // Clear existing playlists
                    userPlaylists.clear();
                    
                    // Add each playlist from the snapshot
                    for (DocumentSnapshot document : value.getDocuments()) {
                        UserPlaylist playlist = playlistManager.convertDocumentToPlaylist(document);
                        if (playlist != null) {
                            userPlaylists.add(playlist);
                            playlistNameMap.put(playlist.getName(), playlist.getName());
                        }
                    }
                    
                    // Update UI with new playlist data
                    updateMyPlaylistUI();
                    
                    Log.d(TAG, "Real-time update: " + userPlaylists.size() + " playlists");
                }
            }
        });
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
    
    /**
     * Helper method to find a user playlist by name
     */
    private UserPlaylist findUserPlaylistByName(String name) {
        for (UserPlaylist playlist : userPlaylists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
    
    private void updateGreeting() {
        userUtils.getUserName(requireContext(), new UserUtils.OnNameFetched() {
            @Override
            public void onNameFetched(String name) {
                String timeOfDay = getTimeOfDay();
                tvGreeting.setText(String.format("%s, %s!", timeOfDay, name));
            }
            
            @Override
            public void onError(String error) {
                // Fallback to generic greeting
                String timeOfDay = getTimeOfDay();
                tvGreeting.setText(String.format("%s!", timeOfDay));
            }
        });
    }
    
    private String getTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hourOfDay < 12) {
            return "Good morning";
        } else if (hourOfDay < 17) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }
} 