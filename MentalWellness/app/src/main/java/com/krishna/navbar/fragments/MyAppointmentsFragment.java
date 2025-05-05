package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krishna.navbar.R;
import com.krishna.navbar.adapters.AppointmentAdapter;
import com.krishna.navbar.models.TherapistBooking;
import com.krishna.navbar.utils.FirestoreHelper;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsFragment extends Fragment {

    // UI components
    private ImageButton btnBack;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    // Data
    private List<TherapistBooking> bookings = new ArrayList<>();
    private AppointmentAdapter adapter;
    private FirestoreHelper firestoreHelper;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);
        
        // Hide bottom navigation when this fragment is shown
        if (getActivity() != null) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        }
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        
        // Load appointments
        loadAppointments();
        
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Ensure we restore the bottom navigation visibility for other fragments
        if (getActivity() != null && !isRemoving()) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.rv_appointments);
        emptyStateText = view.findViewById(R.id.tv_empty_state);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        
        // Initialize Firestore helper
        firestoreHelper = new FirestoreHelper();
        
        // Set up back button
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
    }
    
    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(getContext(), bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.peach,
                R.color.blue,
                R.color.primary
        );
    }
    
    private void loadAppointments() {
        // Show loading
        swipeRefreshLayout.setRefreshing(true);
        
        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            swipeRefreshLayout.setRefreshing(false);
            showEmptyState("Please sign in to view your appointments");
            return;
        }
        
        // Get user ID
        String userId = currentUser.getUid();
        
        // Fetch appointments from Firestore
        firestoreHelper.getUserBookings(userId)
            .addOnSuccessListener(queryDocumentSnapshots -> {
                bookings.clear();
                
                if (queryDocumentSnapshots.isEmpty()) {
                    showEmptyState("You don't have any appointments yet");
                } else {
                    hideEmptyState();
                    
                    for (TherapistBooking booking : queryDocumentSnapshots.toObjects(TherapistBooking.class)) {
                        // Set the document ID to the booking
                        booking.setId(queryDocumentSnapshots.getDocuments().get(bookings.size()).getId());
                        bookings.add(booking);
                    }
                    
                    adapter.notifyDataSetChanged();
                }
                
                swipeRefreshLayout.setRefreshing(false);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState("Error loading appointments");
                swipeRefreshLayout.setRefreshing(false);
            });
    }
    
    private void showEmptyState(String message) {
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateText.setText(message);
    }
    
    private void hideEmptyState() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }
} 