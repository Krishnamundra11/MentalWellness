package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.MainActivity;
import com.krishna.navbar.R;

public class FindExpertFragment extends Fragment {

    private Button btnTherapist, btnPsychiatrist, btnStartNow, btnFind;
    private Spinner spinnerCentre;
    private Button btnFilters;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_expert, container, false);
        
        // Initialize views
        btnTherapist = view.findViewById(R.id.btn_therapist);
        btnPsychiatrist = view.findViewById(R.id.btn_psychiatrist);
        btnStartNow = view.findViewById(R.id.btn_start_now);
        spinnerCentre = view.findViewById(R.id.spinner_select_centre);
        btnFilters = view.findViewById(R.id.btn_filters);
        btnFind = view.findViewById(R.id.btn_find);
        btnBack = view.findViewById(R.id.btn_back);
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up spinner with dummy data
        setupSpinner();
        
        return view;
    }
    
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            // Navigate back to LandingFragment through MainActivity
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new LandingFragment());
                ((MainActivity) getActivity()).bottomNavigation.show(MainActivity.ID_HOME, true);
            }
        });

        // Toggle between Therapist and Psychiatrist
        btnTherapist.setOnClickListener(v -> {
            setActiveExpertType(true);
        });
        
        btnPsychiatrist.setOnClickListener(v -> {
            setActiveExpertType(false);
        });
        
        // Start Now button
        btnStartNow.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Starting the process...", Toast.LENGTH_SHORT).show();
            navigateToTherapistList();
        });
        
        // Filters button
        btnFilters.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filters dialog coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Show filter dialog
        });
        
        // Find button
        btnFind.setOnClickListener(v -> {
            String selectedCentre = spinnerCentre.getSelectedItem().toString();
            Toast.makeText(getContext(), "Finding experts at " + selectedCentre, Toast.LENGTH_SHORT).show();
            navigateToTherapistList();
        });
    }
    
    private void setActiveExpertType(boolean isTherapist) {
        if (isTherapist) {
            btnTherapist.setBackgroundResource(R.drawable.bg_button_active);
            btnPsychiatrist.setBackgroundResource(R.drawable.bg_button_inactive);
        } else {
            btnTherapist.setBackgroundResource(R.drawable.bg_button_inactive);
            btnPsychiatrist.setBackgroundResource(R.drawable.bg_button_active);
        }
    }
    
    private void setupSpinner() {
        // Dummy data for centres
        String[] centres = {"Select Centre", "Wellness Centre", "Mental Health Clinic", "Therapy Center", "Psychiatry Hospital"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, centres);
        spinnerCentre.setAdapter(adapter);
    }
    
    private void navigateToTherapistList() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new TherapistListFragment())
                .addToBackStack(null)
                .commit();
    }
} 