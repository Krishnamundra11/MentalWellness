package com.krishna.navbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.fragments.HomeFragment;
import com.krishna.navbar.fragments.LandingFragment;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MainActivity extends AppCompatActivity {

    private static final int ID_HOME = 1;
    private static final int ID_MEDITATION = 2;
    private static final int ID_MUSIC = 3;
    private static final int ID_SLEEP = 4;
    private static final int ID_PROFILE = 5;
    
    NafisBottomNavigation bottomNavigation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
    }
    
    /**
     * Setup bottom navigation with click listeners
     */
    private void setupBottomNavigation() {
        if (bottomNavigation == null) {
            return;
        }
        
        // Set up bottom navigation
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_HOME, R.drawable.round_home_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_MEDITATION, R.drawable.ic_om_symbol));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_MUSIC, R.drawable.ic_play));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_SLEEP, R.drawable.ic_sleep));
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
                    case ID_MEDITATION:
                        // Meditation icon - load HomeFragment
                        loadFragment(new HomeFragment());
                        break;
                    case ID_MUSIC:
                        // Music icon - To be implemented
                        Toast.makeText(MainActivity.this, "Music feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case ID_SLEEP:
                        // Sleep icon - To be implemented
                        Toast.makeText(MainActivity.this, "Sleep feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case ID_PROFILE:
                        // Profile icon - To be implemented
                        Toast.makeText(MainActivity.this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return null;
            }
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.con, fragment)
                .commit();
    }
}

