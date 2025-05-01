package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Date appointmentDate;
    private String appointmentTime;
    private boolean isOnlineSession;
    
    public static BookingConfirmationFragment newInstance(Therapist therapist, 
                                                          Date appointmentDate, 
                                                          String appointmentTime, 
                                                          boolean isOnlineSession) {
        BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        Bundle args = new Bundle();
        args.putString("name", therapist.getName());
        args.putString("specialization", therapist.getSpecialization());
        args.putInt("profileImage", therapist.getProfileImageResourceId());
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
        setupData();
        setupListeners();
        
        return view;
    }
    
    private void extractArguments() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            
            // Extract therapist data
            String name = args.getString("name", "Dr. Sarah Anderson");
            String specialization = args.getString("specialization", "Clinical Psychologist");
            int profileImageResourceId = args.getInt("profileImage", R.drawable.placeholder_therapist);
            therapist = new Therapist(name, specialization, 0, 0, "", "", "", profileImageResourceId);
            
            // Extract appointment details
            appointmentDate = (Date) args.getSerializable("appointmentDate");
            if (appointmentDate == null) {
                appointmentDate = new Date(); // Default to current date if null
            }
            
            appointmentTime = args.getString("appointmentTime", "10:30 AM");
            isOnlineSession = args.getBoolean("isOnlineSession", true);
        } else {
            // Default data if no arguments passed
            therapist = new Therapist(
                    "Dr. Sarah Anderson",
                    "Clinical Psychologist",
                    0, 0,
                    "",
                    "",
                    "",
                    R.drawable.placeholder_therapist
            );
            appointmentDate = new Date();
            appointmentTime = "10:30 AM";
            isOnlineSession = true;
        }
    }
    
    private void initViews(View view) {
        // Top bar
        btnBack = view.findViewById(R.id.btn_back);
        
        // Success icon
        ivSuccess = view.findViewById(R.id.iv_success);
        
        // Therapist info
        ivTherapist = view.findViewById(R.id.iv_therapist);
        tvTherapistName = view.findViewById(R.id.tv_therapist_name);
        tvTherapistSpecialty = view.findViewById(R.id.tv_therapist_specialty);
        
        // Session details
        tvSessionDate = view.findViewById(R.id.tv_session_date);
        tvSessionTime = view.findViewById(R.id.tv_session_time);
        tvSessionType = view.findViewById(R.id.tv_session_type);
        
        // Action buttons
        btnViewAppointments = view.findViewById(R.id.btn_view_appointments);
        btnBackHome = view.findViewById(R.id.btn_back_home);
    }
    
    private void setupData() {
        // Set therapist info
        tvTherapistName.setText(therapist.getName());
        tvTherapistSpecialty.setText(therapist.getSpecialization());
        ivTherapist.setImageResource(therapist.getProfileImageResourceId());
        
        // Set appointment details
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        tvSessionDate.setText(dateFormat.format(appointmentDate));
        tvSessionTime.setText(appointmentTime);
        tvSessionType.setText(isOnlineSession ? "Online Session" : "In-Person Session");
        
        // Start success icon animation
        animateSuccessIcon();
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
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        
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