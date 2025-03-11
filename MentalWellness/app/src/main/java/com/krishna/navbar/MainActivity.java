package com.krishna.navbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MainActivity extends AppCompatActivity {

//    private static final int ID_HOME = 1;
//    private static final int ID_EXPLORE = 2;
//    private static final int ID_MESSAGE = 3;
//    private static final int ID_NOTIFICATION = 4;
//    private static final int ID_ACCOUNT = 5;
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

        NafisBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);


        // Set up bottom navigation
        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.round_home_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_meditate));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_play));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.ic_sleep));
        bottomNavigation.add(new NafisBottomNavigation.Model(5, R.drawable.ic_profile));


        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {

                if (model.getId() == 1) {
                    startActivity(new Intent(MainActivity.this, ActivityTwo.class));
                } else if (model.getId() == 2) {
                    FragmentTwo fragmentTwo = new FragmentTwo();

                    getSupportFragmentManager().beginTransaction().replace(R.id.con, fragmentTwo).commit();
                } else if (model.getId() == 3) {

                } else if (model.getId() == 4) {

                } else if (model.getId() == 5) {

                }

                return null;
            }
        });

    }}

