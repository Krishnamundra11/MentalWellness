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

import com.bumptech.glide.Glide;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingConfirmationFragment extends Fragment {

    // UI components
    private ImageButton btnBack;
    private CircleImageView ivTherapist;
    private ImageView ivSuccess;
    private TextView tvTherapistName, tvTherapistSpecialty;
    private TextView tvSessionDate, tvSessionTime, tvSessionType;
    private Button btnViewAppointments, btnBackHome;
    
    // Data
    private Therapist therapist;
    private String therapistId;
    private String therapistName;
    private Date appointmentDate;
    private String appointmentTime;
    private boolean isOnlineSession;
    
    public static BookingConfirmationFragment newInstance(Therapist therapist, 
                                                          Date appointmentDate, 
                                                          String appointmentTime, 
                                                          boolean isOnlineSession) {
        BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        Bundle args = new Bundle();
        args.putString("therapistId", therapist.getId());
        args.putString("therapistName", therapist.getName());
        args.putSerializable("appointmentDate", appointmentDate);
        args.putString("appointmentTime", appointmentTime);
        args.putBoolean("isOnlineSession", isOnlineSession);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_confirmation, container, false);
        
        extractArguments();
        initViews(view);
        
        // Show appointment details right away while we fetch therapist data
        setupAppointmentDetails();
        
        // Fetch therapist details from Firestore
        fetchTherapistData();
        
        setupListeners();
        
        return view;
    }
    
    private void extractArguments() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            
            // Extract therapist ID instead of full details
            therapistId = args.getString("therapistId");
            therapistName = args.getString("therapistName", "Dr. Sarah Anderson");
            
            // Extract appointment details
            appointmentDate = (Date) args.getSerializable("appointmentDate");
            if (appointmentDate == null) {
                appointmentDate = new Date(); // Default to current date if null
            }
            
            appointmentTime = args.getString("appointmentTime", "10:30 AM");
            isOnlineSession = args.getBoolean("isOnlineSession", true);
        } else {
            therapistName = "Dr. Sarah Anderson";
            appointmentDate = new Date();
            appointmentTime = "10:30 AM";
            isOnlineSession = true;
        }
    }
    
    private void fetchTherapistData() {
        if (therapistId != null) {
            FirestoreHelper helper = new FirestoreHelper();
            helper.getTherapistById(therapistId)
                .addOnSuccessListener(documentSnapshot -> {
                    therapist = documentSnapshot.toObject(Therapist.class);
                    if (therapist != null) {
                        therapist.setId(documentSnapshot.getId());
                        setupTherapistData();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load therapist details", Toast.LENGTH_SHORT).show();
                });
        }
    }
    
    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        ivSuccess = view.findViewById(R.id.iv_success);
        ivTherapist = view.findViewById(R.id.iv_therapist);
        tvTherapistName = view.findViewById(R.id.tv_therapist_name);
        tvTherapistSpecialty = view.findViewById(R.id.tv_therapist_specialty);
        tvSessionDate = view.findViewById(R.id.tv_session_date);
        tvSessionTime = view.findViewById(R.id.tv_session_time);
        tvSessionType = view.findViewById(R.id.tv_session_type);
        btnViewAppointments = view.findViewById(R.id.btn_view_appointments);
        btnBackHome = view.findViewById(R.id.btn_back_home);
    }
    
    private void setupAppointmentDetails() {
        // Set therapist name initially (we'll update the full details later)
        tvTherapistName.setText(therapistName);
        
        // Set appointment details
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        tvSessionDate.setText(dateFormat.format(appointmentDate));
        tvSessionTime.setText(appointmentTime);
        tvSessionType.setText(isOnlineSession ? "Online Session" : "In-Person Session");
        
        // Start success icon animation
        animateSuccessIcon();
    }
    
    private void setupTherapistData() {
        if (therapist == null) return;
        
        // Update therapist info now that we have full details
        tvTherapistName.setText(therapist.getName() != null ? therapist.getName() : "");
        
        // Handle specialization (now a List<String>)
        if (therapist.getSpecialization() != null && !therapist.getSpecialization().isEmpty()) {
            tvTherapistSpecialty.setText(therapist.getSpecialization().get(0));
        } else {
            tvTherapistSpecialty.setText("");
        }
        
        // Load image from URL
        if (therapist.getProfileImageUrl() != null && !therapist.getProfileImageUrl().isEmpty()) {
            Glide.with(getContext())
                .load(therapist.getProfileImageUrl())
                .into(ivTherapist);
        } else {
            ivTherapist.setImageResource(R.drawable.placeholder_therapist);
        }
    }
    
    private void animateSuccessIcon() {
        // Initially hide the success icon (it will be shown by the animation)
        ivSuccess.setScaleX(0f);
        ivSuccess.setScaleY(0f);
        ivSuccess.setAlpha(0f);
        
        // Start the animation with a slight delay to let the transition animation finish
        ivSuccess.postDelayed(() -> {
            android.animation.Animator animator = android.animation.AnimatorInflater.loadAnimator(getContext(), R.anim.check_animation);
            animator.setTarget(ivSuccess);
            animator.start();
        }, 300);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        btnViewAppointments.setOnClickListener(v -> {
            // Navigate to appointments screen (to be implemented)
            // For now, just go back
            getParentFragmentManager().popBackStack();
        });
        
        btnBackHome.setOnClickListener(v -> {
            // Navigate back to home screen
            // Pop back to the root fragment
            getParentFragmentManager().popBackStack(null, 
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
    }
} 