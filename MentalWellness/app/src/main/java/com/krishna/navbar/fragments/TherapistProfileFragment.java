package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;

public class TherapistProfileFragment extends Fragment {

    // UI components
    private ImageButton btnBack, btnFavorite;
    private ImageView imgProfile;
    private TextView tvName, tvSpecialization, tvRating, tvReviews;
    private Button btnOnline, btnInPerson, btnBookAppointment;
    private TextView tvExperience, tvRatingStat, tvReviewsCount;
    private TextView tvAboutMe, tvReadMore;
    private TextView tvConsultationFee, tvSessionDuration;
    
    // Data
    private Therapist therapist;
    private boolean isFavorite = false;
    private boolean isAboutExpanded = false;
    private boolean isOnlineSelected = true;
    
    public static TherapistProfileFragment newInstance(Therapist therapist) {
        TherapistProfileFragment fragment = new TherapistProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", therapist.getName());
        args.putString("specialization", therapist.getSpecialization());
        args.putFloat("rating", therapist.getRating());
        args.putInt("reviewCount", therapist.getReviewCount());
        args.putString("experience", therapist.getExperience());
        args.putString("languages", therapist.getLanguages());
        args.putBoolean("isFavorite", therapist.isFavorite());
        args.putInt("profileImage", therapist.getProfileImageResourceId());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapist_profile, container, false);
        
        extractArguments();
        initViews(view);
        setupListeners();
        populateData();
        
        return view;
    }
    
    private void extractArguments() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            String name = args.getString("name", "Dr. Sarah Anderson");
            String specialization = args.getString("specialization", "Clinical Psychologist");
            float rating = args.getFloat("rating", 4.9f);
            int reviewCount = args.getInt("reviewCount", 128);
            String experience = args.getString("experience", "8+ years");
            String languages = args.getString("languages", "English, Hindi and Bengali");
            isFavorite = args.getBoolean("isFavorite", false);
            int profileImageResourceId = args.getInt("profileImage", R.drawable.therapist_3);
            
            therapist = new Therapist(name, specialization, rating, reviewCount, 
                    experience, languages, "", profileImageResourceId);
            therapist.setFavorite(isFavorite);
        } else {
            // Default data if no arguments passed
            therapist = new Therapist(
                    "Dr. Sarah Anderson",
                    "Clinical Psychologist",
                    4.9f,
                    128,
                    "8+ years",
                    "English, Hindi and Bengali",
                    "",
                    R.drawable.therapist_3
            );
        }
    }
    
    private void initViews(View view) {
        // Top bar
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        
        // Profile info
        imgProfile = view.findViewById(R.id.img_profile);
        tvName = view.findViewById(R.id.tv_name);
        tvSpecialization = view.findViewById(R.id.tv_specialization);
        tvRating = view.findViewById(R.id.tv_rating);
        tvReviews = view.findViewById(R.id.tv_reviews);
        
        // Consultation mode
        btnOnline = view.findViewById(R.id.btn_online);
        btnInPerson = view.findViewById(R.id.btn_in_person);
        
        // Stats
        tvExperience = view.findViewById(R.id.tv_experience);
        tvRatingStat = view.findViewById(R.id.tv_rating_stat);
        tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        
        // About me
        tvAboutMe = view.findViewById(R.id.tv_about_me);
        tvReadMore = view.findViewById(R.id.tv_read_more);
        
        // Consultation info
        tvConsultationFee = view.findViewById(R.id.tv_consultation_fee);
        tvSessionDuration = view.findViewById(R.id.tv_session_duration);
        
        // CTA button
        btnBookAppointment = view.findViewById(R.id.btn_book_appointment);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        
        btnFavorite.setOnClickListener(v -> {
            toggleFavorite();
        });
        
        btnOnline.setOnClickListener(v -> {
            setConsultationMode(true);
        });
        
        btnInPerson.setOnClickListener(v -> {
            setConsultationMode(false);
        });
        
        tvReadMore.setOnClickListener(v -> {
            toggleAboutExpansion();
        });
        
        btnBookAppointment.setOnClickListener(v -> {
            // Navigate to booking screen
            BookAppointmentFragment bookingFragment = BookAppointmentFragment.newInstance(therapist);
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, bookingFragment)
                .addToBackStack(null)
                .commit();
        });
    }
    
    private void populateData() {
        // Set profile data
        imgProfile.setImageResource(therapist.getProfileImageResourceId());
        tvName.setText(therapist.getName());
        tvSpecialization.setText(therapist.getSpecialization());
        tvRating.setText(String.valueOf(therapist.getRating()));
        tvReviews.setText("(" + therapist.getReviewCount() + " reviews)");
        
        // Set stats
        String experience = therapist.getExperience().replace(" years", "").replace(" year", "");
        tvExperience.setText(experience);
        tvRatingStat.setText(String.valueOf(therapist.getRating()));
        tvReviewsCount.setText(String.valueOf(therapist.getReviewCount()));
        
        // Set favorite icon
        updateFavoriteIcon();
        
        // Set consultation mode (online by default)
        setConsultationMode(isOnlineSelected);
    }
    
    private void toggleFavorite() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        
        String message = isFavorite ? 
                "Added " + therapist.getName() + " to favorites" : 
                "Removed " + therapist.getName() + " from favorites";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    private void updateFavoriteIcon() {
        btnFavorite.setImageResource(isFavorite ? 
                R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }
    
    private void setConsultationMode(boolean isOnline) {
        isOnlineSelected = isOnline;
        
        // Update button backgrounds
        btnOnline.setBackgroundResource(isOnline ? 
                R.drawable.bg_button_active : R.drawable.bg_button_inactive);
                
        btnInPerson.setBackgroundResource(!isOnline ? 
                R.drawable.bg_button_active : R.drawable.bg_button_inactive);
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