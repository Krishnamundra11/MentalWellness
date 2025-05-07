package com.krishna.navbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.fragments.FindExpertFragment;
import com.krishna.navbar.fragments.HomeFragment;
import com.krishna.navbar.fragments.LandingFragment;
import com.krishna.navbar.fragments.MusicMainFragment;
import com.krishna.navbar.fragments.ProfileFragment;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    public static final int ID_MUSIC = 1;
    public static final int ID_MEDITATE = 2;
    public static final int ID_HOME = 3;
    public static final int ID_THERAPY = 4;
    public static final int ID_PROFILE = 5;
    
    public NafisBottomNavigation bottomNavigation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Make status bar transparent but keep content below it
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        window.setStatusBarColor(0x00000000);
        
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Setup bottom navigation
        setupBottomNavigation();
        
        // Check if we have a selected tab from intent (coming from other activities)
        if (getIntent().hasExtra("selected_tab_id")) {
            int selectedTabId = getIntent().getIntExtra("selected_tab_id", ID_HOME);
            // Show the selected tab
            bottomNavigation.show(selectedTabId, true);
            // Load the appropriate fragment
            loadFragmentForTabId(selectedTabId);
        }

        FirebaseApp.initializeApp(this);
    }
    
    /**
     * Setup bottom navigation with click listeners
     */
    private void setupBottomNavigation() {
        if (bottomNavigation == null) {
            return;
        }
        
        // Set up bottom navigation
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_MUSIC, R.drawable.ic_music));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_MEDITATE, R.drawable.ic_meditation));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_HOME, R.drawable.round_home_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_THERAPY, R.drawable.ic_therapy));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_PROFILE, R.drawable.ic_profile));

        // Programmatically select the Home tab with animation
        bottomNavigation.show(ID_HOME, true);
        
        // Load the LandingFragment by default
        loadFragment(new LandingFragment());
        
        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                switch (model.getId()) {
                    case ID_HOME:
                        // Home icon - load LandingFragment
                        loadFragment(new LandingFragment());
                        break;
                    case ID_MEDITATE:
                        // Meditation icon - load HomeFragment
                        loadFragment(new HomeFragment());
                        break;
                    case ID_MUSIC:
                        // Music icon - load MusicMainFragment
                        loadFragment(new MusicMainFragment());
                        break;
                    case ID_THERAPY:
                        // Therapy icon - load FindExpertFragment
                        loadFragment(new FindExpertFragment());
                        break;
                    case ID_PROFILE:
                        // Load ProfileFragment instead of launching ProfileActivity
                        loadFragment(new ProfileFragment());
                        break;
                }
                return null;
            }
        });
    }
    
    /**
     * Load appropriate fragment based on tab ID
     */
    private void loadFragmentForTabId(int tabId) {
        switch (tabId) {
            case ID_HOME:
                loadFragment(new LandingFragment());
                break;
            case ID_MEDITATE:
                loadFragment(new HomeFragment());
                break;
            case ID_MUSIC:
                loadFragment(new MusicMainFragment());
                break;
            case ID_THERAPY:
                loadFragment(new FindExpertFragment());
                break;
            case ID_PROFILE:
                loadFragment(new ProfileFragment());
                break;
        }
    }
    
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.con, fragment)
                .commit();
    }
}

