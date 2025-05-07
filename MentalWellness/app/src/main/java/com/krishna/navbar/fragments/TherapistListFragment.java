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
import com.krishna.navbar.utils.FirestoreHelper;
import com.krishna.navbar.utils.NavigationHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        loadTherapistsFromFirestore();
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
            NavigationHelper.handleBackNavigation(this);
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
    
    private void loadTherapistsFromFirestore() {
        FirestoreHelper helper = new FirestoreHelper();
        // For demo, only specialization filter is used. You can add language/mode as needed.
        String filter = currentCategory.equals("all") ? null : currentCategory;
        helper.getTherapists(filter, null, null)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allTherapists = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Therapist therapist = doc.toObject(Therapist.class);
                    if (therapist != null) {
                        therapist.setId(doc.getId());
                        allTherapists.add(therapist);
                    }
                }
                filteredTherapists = new ArrayList<>(allTherapists);
                adapter.updateList(filteredTherapists);
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load therapists", Toast.LENGTH_SHORT).show());
    }
    
    private void setupRecyclerView() {
        if (filteredTherapists == null) {
            filteredTherapists = new ArrayList<>();
        }
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
        
        loadTherapistsFromFirestore();
    }
    
    private void filterTherapists() {
        filteredTherapists = new ArrayList<>();
        for (Therapist therapist : allTherapists) {
            boolean categoryMatch = currentCategory.equals("all") || 
                (therapist.getSpecialization() != null && therapist.getSpecialization().toString().toLowerCase(Locale.ROOT).contains(currentCategory));
            boolean searchMatch = searchQuery.isEmpty() || 
                (therapist.getName() != null && therapist.getName().toLowerCase(Locale.ROOT).contains(searchQuery)) ||
                (therapist.getSpecialization() != null && therapist.getSpecialization().toString().toLowerCase(Locale.ROOT).contains(searchQuery));
            if (categoryMatch && searchMatch) {
                filteredTherapists.add(therapist);
            }
        }
        adapter.updateList(filteredTherapists);
    }

    @Override
    public void onProfileClick(Therapist therapist, int position) {
        // Navigate to TherapistProfileFragment with the selected therapist ID
        TherapistProfileFragment profileFragment = TherapistProfileFragment.newInstance(therapist.getId());
        NavigationHelper.navigateToFragment(this, profileFragment, true);
    }

    @Override
    public void onFavoriteToggle(Therapist therapist, int position) {
        boolean isFavorite = adapter.isFavorite(therapist);
        String message = isFavorite ? 
                "Added " + therapist.getName() + " to favorites" : 
                "Removed " + therapist.getName() + " from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
} 