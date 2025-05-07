package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;
import com.krishna.navbar.utils.FirestoreHelper;
import com.google.firebase.firestore.DocumentSnapshot;

public class TherapistProfileFragment extends Fragment {

    // UI components
    private ImageButton btnBack, btnFavorite;
    private ImageView imgProfile;
    private TextView tvName, tvSpecialization, tvRating, tvReviews;
    private Button btnOnline, btnInPerson, btnBookAppointment;
    private TextView tvExperience, tvRatingStat, tvReviewsCount;
    private TextView tvAboutMe, tvReadMore;
    private TextView tvConsultationFee, tvSessionDuration;
    private ProgressBar progressLoading;
    
    // Data
    private Therapist therapist;
    private boolean isFavorite = false;
    private boolean isAboutExpanded = false;
    private boolean isOnlineSelected = true;
    private String therapistId;

    public static TherapistProfileFragment newInstance(String therapistId) {
        TherapistProfileFragment fragment = new TherapistProfileFragment();
        Bundle args = new Bundle();
        args.putString("therapistId", therapistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapist_profile, container, false);
        
        // Hide bottom navigation when this fragment is shown
        if (getActivity() != null) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        }
        
        // Extract therapist ID from arguments
        if (getArguments() != null) {
            therapistId = getArguments().getString("therapistId");
        }
        
        initViews(view);
        setupListeners();
        fetchTherapistFromFirestore();
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

    private void fetchTherapistFromFirestore() {
        if (therapistId == null) {
            Toast.makeText(getContext(), "Therapist not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading indicator
        if (progressLoading != null) {
            progressLoading.setVisibility(View.VISIBLE);
        }
        
        FirestoreHelper helper = new FirestoreHelper();
        
        // Set a timeout for the operation - 10 seconds
        com.google.firebase.firestore.FirebaseFirestoreSettings settings = 
            new com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // Enable offline persistence
                .build();
        com.google.firebase.firestore.FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        
        helper.getTherapistById(therapistId)
            .addOnSuccessListener(documentSnapshot -> {
                // Hide loading indicator
                if (progressLoading != null) {
                    progressLoading.setVisibility(View.GONE);
                }
                
                therapist = documentSnapshot.toObject(Therapist.class);
                if (therapist != null) {
                    therapist.setId(documentSnapshot.getId());
                    populateData();
                } else {
                    Toast.makeText(getContext(), "Therapist not found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                // Hide loading indicator
                if (progressLoading != null) {
                    progressLoading.setVisibility(View.GONE);
                }
                
                String errorMsg = "Failed to load therapist";
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("DEADLINE_EXCEEDED") || 
                        e.getMessage().contains("timed out")) {
                        errorMsg = "Connection timed out. Please check your internet connection and try again.";
                    }
                }
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
            });
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        imgProfile = view.findViewById(R.id.img_profile);
        tvName = view.findViewById(R.id.tv_name);
        tvSpecialization = view.findViewById(R.id.tv_specialization);
        tvRating = view.findViewById(R.id.tv_rating);
        tvReviews = view.findViewById(R.id.tv_reviews);
        btnOnline = view.findViewById(R.id.btn_online);
        btnInPerson = view.findViewById(R.id.btn_in_person);
        tvExperience = view.findViewById(R.id.tv_experience);
        tvRatingStat = view.findViewById(R.id.tv_rating_stat);
        tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        tvAboutMe = view.findViewById(R.id.tv_about_me);
        tvReadMore = view.findViewById(R.id.tv_read_more);
        tvConsultationFee = view.findViewById(R.id.tv_consultation_fee);
        tvSessionDuration = view.findViewById(R.id.tv_session_duration);
        btnBookAppointment = view.findViewById(R.id.btn_book_appointment);
        
        // Initialize progress bar if it exists in layout
        progressLoading = view.findViewById(R.id.progress_loading);
        if (progressLoading == null) {
            // If the progress bar doesn't exist in the layout, we'll manage without it
            android.util.Log.w("TherapistProfile", "Progress loading view not found in layout");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnOnline.setOnClickListener(v -> setConsultationMode(true));
        btnInPerson.setOnClickListener(v -> setConsultationMode(false));
        tvReadMore.setOnClickListener(v -> toggleAboutExpansion());
        btnBookAppointment.setOnClickListener(v -> {
            if (therapist != null) {
                BookAppointmentFragment bookingFragment = BookAppointmentFragment.newInstance(therapist.getId());
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.con, bookingFragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
    }

    private void populateData() {
        if (therapist == null) return;
        // Load image from URL if available
        if (therapist.getProfileImageUrl() != null && !therapist.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(therapist.getProfileImageUrl()).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.placeholder_therapist);
        }
        tvName.setText(therapist.getName());
        tvSpecialization.setText(therapist.getSpecialization() != null ? therapist.getSpecialization().toString() : "");
        tvRating.setText(String.valueOf(therapist.getRating()));
        tvReviews.setText("(" + therapist.getReviewCount() + " reviews)");
        tvExperience.setText(therapist.getExperience());
        tvRatingStat.setText(String.valueOf(therapist.getRating()));
        tvReviewsCount.setText(String.valueOf(therapist.getReviewCount()));
        tvAboutMe.setText(therapist.getAboutMe());
        tvConsultationFee.setText("₹" + therapist.getFee());
        // You can set session duration if you have it in Firestore
        // Set favorite icon
        updateFavoriteIcon();
        setConsultationMode(isOnlineSelected);
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        String message = isFavorite ? "Added " + (therapist != null ? therapist.getName() : "") + " to favorites" : "Removed " + (therapist != null ? therapist.getName() : "") + " from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteIcon() {
        btnFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void setConsultationMode(boolean isOnline) {
        isOnlineSelected = isOnline;
        btnOnline.setBackgroundResource(isOnline ? R.drawable.bg_button_active : R.drawable.bg_button_inactive);
        btnInPerson.setBackgroundResource(!isOnline ? R.drawable.bg_button_active : R.drawable.bg_button_inactive);
    }

    private void toggleAboutExpansion() {
        isAboutExpanded = !isAboutExpanded;
        if (isAboutExpanded) {
            tvAboutMe.setMaxLines(Integer.MAX_VALUE);
            tvAboutMe.setEllipsize(null);
            tvReadMore.setText("Read less ↑");
        } else {
            tvAboutMe.setMaxLines(3);
            tvAboutMe.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tvReadMore.setText("Read more →");
        }
    }
} 