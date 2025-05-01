package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.TherapistAdapter;
import com.krishna.navbar.models.Therapist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TherapistListFragment extends Fragment implements TherapistAdapter.OnTherapistClickListener {

    private RecyclerView rvTherapists;
    private EditText etSearch;
    private Button btnAllSpecialists, btnPsychologists, btnPsychiatrists;
    private ImageButton btnBack, btnFilter;
    
    private List<Therapist> allTherapists;
    private List<Therapist> filteredTherapists;
    private TherapistAdapter adapter;
    
    private String currentCategory = "all";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapist_list, container, false);
        
        initViews(view);
        setupListeners();
        loadDummyData();
        setupRecyclerView();
        
        return view;
    }
    
    private void initViews(View view) {
        rvTherapists = view.findViewById(R.id.rv_therapists);
        etSearch = view.findViewById(R.id.et_search);
        btnAllSpecialists = view.findViewById(R.id.btn_all_specialists);
        btnPsychologists = view.findViewById(R.id.btn_psychologists);
        btnPsychiatrists = view.findViewById(R.id.btn_psychiatrists);
        btnBack = view.findViewById(R.id.btn_back);
        btnFilter = view.findViewById(R.id.btn_filter);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter options coming soon", Toast.LENGTH_SHORT).show();
        });
        
        btnAllSpecialists.setOnClickListener(v -> {
            setActiveCategory("all");
        });
        
        btnPsychologists.setOnClickListener(v -> {
            setActiveCategory("psychologist");
        });
        
        btnPsychiatrists.setOnClickListener(v -> {
            setActiveCategory("psychiatrist");
        });
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                filterTherapists();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void loadDummyData() {
        allTherapists = new ArrayList<>();
        
        // Add dummy data
        allTherapists.add(new Therapist(
                "Dr. Sarah Johnson",
                "Clinical Psychologist",
                4.9f,
                120,
                "15+ years",
                "English, Spanish",
                "Today, 3:00 PM",
                R.drawable.therapist_1
        ));
        
        allTherapists.add(new Therapist(
                "Dr. Michael Chen",
                "Psychiatrist",
                4.8f,
                95,
                "10+ years",
                "English, Mandarin",
                "Tomorrow, 10:00 AM",
                R.drawable.therapist_2
        ));
        
        allTherapists.add(new Therapist(
                "Dr. Sarah Anderson",
                "Psychiatrist",
                4.8f,
                95,
                "10+ years",
                "English, Mandarin",
                "Tomorrow, 10:00 AM",
                R.drawable.therapist_3
        ));
        
        filteredTherapists = new ArrayList<>(allTherapists);
    }
    
    private void setupRecyclerView() {
        adapter = new TherapistAdapter(getContext(), filteredTherapists, this);
        rvTherapists.setAdapter(adapter);
    }
    
    private void setActiveCategory(String category) {
        currentCategory = category;
        
        // Update button backgrounds
        btnAllSpecialists.setBackgroundResource(category.equals("all") ? 
                R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
                
        btnPsychologists.setBackgroundResource(category.equals("psychologist") ? 
                R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
                
        btnPsychiatrists.setBackgroundResource(category.equals("psychiatrist") ? 
                R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        
        filterTherapists();
    }
    
    private void filterTherapists() {
        filteredTherapists = allTherapists.stream()
                .filter(therapist -> {
                    boolean categoryMatch = currentCategory.equals("all") || 
                            therapist.getSpecialization().toLowerCase().contains(currentCategory);
                    
                    boolean searchMatch = searchQuery.isEmpty() || 
                            therapist.getName().toLowerCase().contains(searchQuery) || 
                            therapist.getSpecialization().toLowerCase().contains(searchQuery);
                    
                    return categoryMatch && searchMatch;
                })
                .collect(Collectors.toList());
        
        adapter.updateList(filteredTherapists);
    }

    @Override
    public void onProfileClick(Therapist therapist, int position) {
        // Navigate to TherapistProfileFragment with the selected therapist
        TherapistProfileFragment profileFragment = TherapistProfileFragment.newInstance(therapist);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, profileFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFavoriteToggle(Therapist therapist, int position) {
        String message = therapist.isFavorite() ? 
                "Added " + therapist.getName() + " to favorites" : 
                "Removed " + therapist.getName() + " from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
} 