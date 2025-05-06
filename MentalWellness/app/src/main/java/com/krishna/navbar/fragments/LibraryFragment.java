package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.krishna.navbar.R;
import com.krishna.navbar.utils.FirebaseFavoritesManager;

public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";

    private FrameLayout fragmentContainer;
    private Fragment currentFragment;
    
    // Tab position constants
    private static final int TAB_ALL = 0;
    private static final int TAB_RECENTLY_PLAYED = 1;
    private static final int TAB_FAVORITES = 2;
    
    // Keep track of current tab
    private int currentTabPosition = TAB_ALL;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_library, container, false);
        Log.d(TAG, "onCreateView: Inflating LibraryFragment");
        setupUI(view);
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: LibraryFragment resumed, refreshing current tab: " + currentTabPosition);
        // Refresh current tab when resuming
        refreshCurrentTab();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: LibraryFragment view created");
        
        // Force selection of the tab right after view creation to ensure proper initialization
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        if (tabLayout != null) {
            tabLayout.getTabAt(TAB_FAVORITES).select();
            Log.d(TAG, "onViewCreated: Forced selection of FAVORITES tab");
        }
    }

    private void setupUI(View view) {
        Log.d(TAG, "setupUI: Setting up Library UI");
        fragmentContainer = view.findViewById(R.id.fragmentContainer);
        
        // Set up tab layout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                Log.d(TAG, "onTabSelected: Selected tab position: " + currentTabPosition);
                
                // Switch fragment based on selected tab
                switch (tab.getPosition()) {
                    case TAB_ALL:
                        Log.d(TAG, "onTabSelected: Showing ALL tab content");
                        showAllContent();
                        break;
                    case TAB_RECENTLY_PLAYED:
                        Log.d(TAG, "onTabSelected: Showing RECENTLY PLAYED tab content");
                        showRecentlyPlayedFragment();
                        break;
                    case TAB_FAVORITES:
                        Log.d(TAG, "onTabSelected: Showing FAVORITES tab content");
                        showFavoritesFragment();
                        break;
                }
                
                // Toast for debugging
                String tabName = "Unknown";
                if (tab.getPosition() == TAB_ALL) tabName = "All";
                else if (tab.getPosition() == TAB_RECENTLY_PLAYED) tabName = "Recently Played";
                else if (tab.getPosition() == TAB_FAVORITES) tabName = "Favorites";
                
                Toast.makeText(getContext(), "Switched to " + tabName + " tab", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Refresh the current tab content when re-selected
                Log.d(TAG, "onTabReselected: Tab reselected at position: " + tab.getPosition());
                refreshCurrentTab();
            }
        });

        // Set up create playlist button
        View btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);
        if (btnCreatePlaylist != null) {
            btnCreatePlaylist.setOnClickListener(v -> createNewPlaylist());
        } else {
            Log.e(TAG, "setupUI: Create playlist button not found!");
        }

        // Set up playlist card click listeners
        View cardZenPlaylist = view.findViewById(R.id.cardZenPlaylist);
        View cardMorningPlaylist = view.findViewById(R.id.cardMorningPlaylist);

        if (cardZenPlaylist != null) {
            cardZenPlaylist.setOnClickListener(v -> openPlaylist("My Zen Space"));
        }
        
        if (cardMorningPlaylist != null) {
            cardMorningPlaylist.setOnClickListener(v -> openPlaylist("Morning Energy"));
        }
    }
    
    private void refreshCurrentTab() {
        Log.d(TAG, "refreshCurrentTab: Refreshing tab at position: " + currentTabPosition);
        switch (currentTabPosition) {
            case TAB_ALL:
                showAllContent();
                break;
            case TAB_RECENTLY_PLAYED:
                if (currentFragment instanceof RecentlyPlayedFragment) {
                    Log.d(TAG, "refreshCurrentTab: Refreshing existing RecentlyPlayedFragment");
                    ((RecentlyPlayedFragment) currentFragment).refreshContent();
                } else {
                    Log.d(TAG, "refreshCurrentTab: Creating new RecentlyPlayedFragment");
                    showRecentlyPlayedFragment();
                }
                break;
            case TAB_FAVORITES:
                if (currentFragment instanceof FavoritesFragment) {
                    Log.d(TAG, "refreshCurrentTab: Refreshing existing FavoritesFragment");
                    ((FavoritesFragment) currentFragment).refreshFavorites();
                } else {
                    Log.d(TAG, "refreshCurrentTab: Creating new FavoritesFragment");
                    showFavoritesFragment();
                }
                break;
        }
    }

    private void showAllContent() {
        Log.d(TAG, "showAllContent: Showing ALL tab content");
        
        // Show the regular content section
        View view = getView();
        if (view != null) {
            View mainContent = view.findViewById(R.id.mainContent);
            if (mainContent != null) {
                mainContent.setVisibility(View.VISIBLE);
                Log.d(TAG, "showAllContent: Main content set to VISIBLE");
            } else {
                Log.e(TAG, "showAllContent: mainContent view is null!");
            }
        } else {
            Log.e(TAG, "showAllContent: Fragment view is null!");
        }
        
        // Hide the fragment container
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
            Log.d(TAG, "showAllContent: Fragment container set to GONE");
        } else {
            Log.e(TAG, "showAllContent: fragmentContainer is null!");
        }
        
        // Remove any current fragment
        if (currentFragment != null) {
            Log.d(TAG, "showAllContent: Removing current fragment: " + currentFragment.getClass().getSimpleName());
            getChildFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commitNow();
            currentFragment = null;
        } else {
            Log.d(TAG, "showAllContent: No current fragment to remove");
        }
    }

    private void showRecentlyPlayedFragment() {
        Log.d(TAG, "showRecentlyPlayedFragment: Showing RECENTLY PLAYED tab content");
        
        // Hide the main content section
        View view = getView();
        if (view != null) {
            View mainContent = view.findViewById(R.id.mainContent);
            if (mainContent != null) {
                mainContent.setVisibility(View.GONE);
                Log.d(TAG, "showRecentlyPlayedFragment: Main content set to GONE");
            } else {
                Log.e(TAG, "showRecentlyPlayedFragment: mainContent view is null!");
            }
        } else {
            Log.e(TAG, "showRecentlyPlayedFragment: Fragment view is null!");
        }
        
        // Show the fragment container
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            Log.d(TAG, "showRecentlyPlayedFragment: Fragment container set to VISIBLE");
            
            // Create and show the recently played fragment
            RecentlyPlayedFragment fragment = new RecentlyPlayedFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commitNow();
            currentFragment = fragment;
            Log.d(TAG, "showRecentlyPlayedFragment: RecentlyPlayedFragment added to container");
        } else {
            Log.e(TAG, "showRecentlyPlayedFragment: fragmentContainer is null!");
        }
    }

    private void showFavoritesFragment() {
        try {
            Log.d(TAG, "showFavoritesFragment: Showing FAVORITES tab content");
            
            // Hide the main content section
            View view = getView();
            if (view != null) {
                View mainContent = view.findViewById(R.id.mainContent);
                if (mainContent != null) {
                    mainContent.setVisibility(View.GONE);
                    Log.d(TAG, "showFavoritesFragment: Main content set to GONE");
                } else {
                    Log.e(TAG, "showFavoritesFragment: mainContent view is null!");
                }
            } else {
                Log.e(TAG, "showFavoritesFragment: Fragment view is null!");
                return; // Can't proceed without a view
            }
            
            // Show the fragment container - CRITICAL for visibility
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
                Log.d(TAG, "showFavoritesFragment: Fragment container set to VISIBLE");
                
                // Create and show the favorites fragment
                FavoritesFragment fragment = new FavoritesFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commitNow();
                currentFragment = fragment;
                Log.d(TAG, "showFavoritesFragment: FavoritesFragment added to container");
                
                // Force refresh the favorites after fragment is created
                // This is critical for loading the data
                if (fragment.isAdded()) {
                    Log.d(TAG, "showFavoritesFragment: Fragment is added, refreshing favorites");
                    fragment.refreshFavorites();
                } else {
                    Log.d(TAG, "showFavoritesFragment: Fragment not yet added, will refresh in onResume");
                }
            } else {
                Log.e(TAG, "showFavoritesFragment: fragmentContainer is null!");
                Toast.makeText(getContext(), "Cannot show favorites - container missing", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing favorites fragment", e);
            Toast.makeText(getContext(), "Error loading favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void createNewPlaylist() {
        try {
            // Create a new playlist dialog
            CreatePlaylistDialogFragment dialog = new CreatePlaylistDialogFragment();
            dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
        } catch (Exception e) {
            // Log the error and show a toast
            Log.e(TAG, "Error creating playlist", e);
            Toast.makeText(getContext(), "Error creating playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openPlaylist(String playlistName) {
        try {
            // Open playlist fragment with list of tracks
            PlaylistFragment playlistFragment = new PlaylistFragment();
            Bundle args = new Bundle();
            args.putString("playlistName", playlistName);
            playlistFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.con, playlistFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Error opening playlist", e);
            Toast.makeText(getContext(), "Error opening playlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 