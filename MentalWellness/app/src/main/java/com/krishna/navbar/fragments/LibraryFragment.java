package com.krishna.navbar.fragments;

import android.os.Bundle;
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
import com.krishna.navbar.utils.FavoritesManager;

public class LibraryFragment extends Fragment {

    private FrameLayout fragmentContainer;
    private Fragment currentFragment;
    
    // Tab position constants
    private static final int TAB_ALL = 0;
    private static final int TAB_RECENTLY_PLAYED = 1;
    private static final int TAB_FAVORITES = 2;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_library, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        fragmentContainer = view.findViewById(R.id.fragmentContainer);
        
        // Set up tab layout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Switch fragment based on selected tab
                switch (tab.getPosition()) {
                    case TAB_ALL:
                        showAllContent();
                        break;
                    case TAB_RECENTLY_PLAYED:
                        showRecentlyPlayedFragment();
                        break;
                    case TAB_FAVORITES:
                        showFavoritesFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });

        // Set up create playlist button
        View btnCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);
        btnCreatePlaylist.setOnClickListener(v -> createNewPlaylist());

        // Set up playlist card click listeners
        View cardZenPlaylist = view.findViewById(R.id.cardZenPlaylist);
        View cardMorningPlaylist = view.findViewById(R.id.cardMorningPlaylist);

        if (cardZenPlaylist != null) {
            cardZenPlaylist.setOnClickListener(v -> openPlaylist("My Zen Space"));
        }
        
        if (cardMorningPlaylist != null) {
            cardMorningPlaylist.setOnClickListener(v -> openPlaylist("Morning Energy"));
        }

        // Default to showing All content
        showAllContent();
    }

    private void showAllContent() {
        // Show the regular content section
        View view = getView();
        if (view != null) {
            View mainContent = view.findViewById(R.id.mainContent);
            if (mainContent != null) {
                mainContent.setVisibility(View.VISIBLE);
            }
        }
        
        // Hide the fragment container
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }
        
        // Remove any current fragment
        if (currentFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commitNow();
            currentFragment = null;
        }
    }

    private void showRecentlyPlayedFragment() {
        // Hide the main content section
        View view = getView();
        if (view != null) {
            View mainContent = view.findViewById(R.id.mainContent);
            if (mainContent != null) {
                mainContent.setVisibility(View.GONE);
            }
        }
        
        // Show the fragment container
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            
            // Create and show the recently played fragment
            RecentlyPlayedFragment fragment = new RecentlyPlayedFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commitNow();
            currentFragment = fragment;
        }
    }

    private void showFavoritesFragment() {
        // Hide the main content section
        View view = getView();
        if (view != null) {
            View mainContent = view.findViewById(R.id.mainContent);
            if (mainContent != null) {
                mainContent.setVisibility(View.GONE);
            }
        }
        
        // Show the fragment container
        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            
            // Create and show the favorites fragment
            FavoritesFragment fragment = new FavoritesFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commitNow();
            currentFragment = fragment;
        }
    }
    
    private void createNewPlaylist() {
        try {
            // Create a new playlist dialog
            CreatePlaylistDialogFragment dialog = new CreatePlaylistDialogFragment();
            dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
        } catch (Exception e) {
            // Log the error and show a toast
            e.printStackTrace();
            Toast.makeText(getContext(), "Error opening playlist creator: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openPlaylist(String playlistName) {
        // Open playlist fragment with list of tracks
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString("playlistName", playlistName);
        playlistFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playlistFragment)
                .addToBackStack(null)
                .commit();
    }
} 