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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krishna.navbar.R;
import com.krishna.navbar.adapters.DateAdapter;
import com.krishna.navbar.adapters.TimeSlotAdapter;
import com.krishna.navbar.dialogs.AppointmentConfirmationDialog;
import com.krishna.navbar.models.DateItem;
import com.krishna.navbar.models.Therapist;
import com.krishna.navbar.models.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookAppointmentFragment extends Fragment {

    // UI components
    private ImageButton btnBack;
    private CircleImageView ivTherapist;
    private TextView tvTherapistName, tvTherapistSpecialty, tvTherapistExperience;
    private Button btnOnline, btnInPerson, btnConfirm, btnAddCalendar;
    private RecyclerView rvDates, rvTimeSlots;
    
    // Adapters
    private DateAdapter dateAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    
    // Data
    private Therapist therapist;
    private boolean isOnlineSelected = true;
    private DateItem selectedDate;
    private TimeSlot selectedTimeSlot;
    
    public static BookAppointmentFragment newInstance(Therapist therapist) {
        BookAppointmentFragment fragment = new BookAppointmentFragment();
        Bundle args = new Bundle();
        args.putString("name", therapist.getName());
        args.putString("specialization", therapist.getSpecialization());
        args.putString("experience", therapist.getExperience());
        args.putInt("profileImage", therapist.getProfileImageResourceId());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);
        
        extractArguments();
        initViews(view);
        setupData();
        setupListeners();
        
        return view;
    }
    
    private void extractArguments() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            String name = args.getString("name", "Dr. Sarah Anderson");
            String specialization = args.getString("specialization", "Clinical Psychologist");
            String experience = args.getString("experience", "8+ years");
            int profileImageResourceId = args.getInt("profileImage", R.drawable.placeholder_therapist);
            
            therapist = new Therapist(name, specialization, 0, 0, 
                    experience, "", "", profileImageResourceId);
        } else {
            // Default data if no arguments passed
            therapist = new Therapist(
                    "Dr. Sarah Anderson",
                    "Clinical Psychologist",
                    0, 0,
                    "8+ years",
                    "",
                    "",
                    R.drawable.placeholder_therapist
            );
        }
    }
    
    private void initViews(View view) {
        // Top bar
        btnBack = view.findViewById(R.id.btn_back);
        
        // Therapist info
        ivTherapist = view.findViewById(R.id.iv_therapist);
        tvTherapistName = view.findViewById(R.id.tv_therapist_name);
        tvTherapistSpecialty = view.findViewById(R.id.tv_therapist_specialty);
        tvTherapistExperience = view.findViewById(R.id.tv_therapist_experience);
        
        // Session type
        btnOnline = view.findViewById(R.id.btn_online);
        btnInPerson = view.findViewById(R.id.btn_in_person);
        
        // Date and time selection
        rvDates = view.findViewById(R.id.rv_dates);
        rvTimeSlots = view.findViewById(R.id.rv_time_slots);
        
        // Action buttons
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnAddCalendar = view.findViewById(R.id.btn_add_calendar);
    }
    
    private void setupData() {
        // Set therapist info
        tvTherapistName.setText(therapist.getName());
        tvTherapistSpecialty.setText(therapist.getSpecialization());
        tvTherapistExperience.setText(therapist.getExperience());
        ivTherapist.setImageResource(therapist.getProfileImageResourceId());
        
        // Set session type (online by default)
        updateSessionType(isOnlineSelected);
        
        // Setup date recycler view
        setupDateRecyclerView();
        
        // Setup time slots recycler view
        setupTimeSlotRecyclerView();
    }
    
    private void setupDateRecyclerView() {
        List<DateItem> dateItems = generateNextFiveDays();
        dateAdapter = new DateAdapter(dateItems);
        dateAdapter.setOnDateSelectedListener(dateItem -> {
            selectedDate = dateItem;
            // Refresh time slots if needed based on the selected date
        });
        
        rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDates.setAdapter(dateAdapter);
        
        // Select the first date by default
        if (!dateItems.isEmpty()) {
            selectedDate = dateItems.get(0);
            dateAdapter.selectDate(0);
        }
    }
    
    private void setupTimeSlotRecyclerView() {
        List<TimeSlot> timeSlots = generateTimeSlots();
        timeSlotAdapter = new TimeSlotAdapter(timeSlots);
        timeSlotAdapter.setOnTimeSlotSelectedListener(timeSlot -> {
            selectedTimeSlot = timeSlot;
        });
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvTimeSlots.setLayoutManager(layoutManager);
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }
    
    private List<DateItem> generateNextFiveDays() {
        List<DateItem> dateItems = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dayNumberFormat = new SimpleDateFormat("dd", Locale.getDefault());
        
        for (int i = 0; i < 5; i++) {
            Date date = calendar.getTime();
            String dayName = dayFormat.format(date).toUpperCase();
            String dayNumber = dayNumberFormat.format(date);
            
            dateItems.add(new DateItem(dayName, dayNumber, date));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return dateItems;
    }
    
    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        
        // Generate some sample time slots
        timeSlots.add(new TimeSlot("09:00 AM"));
        timeSlots.add(new TimeSlot("10:30 AM"));
        timeSlots.add(new TimeSlot("1:30 PM"));
        timeSlots.add(new TimeSlot("3:30 PM"));
        
        return timeSlots;
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        
        btnOnline.setOnClickListener(v -> {
            updateSessionType(true);
        });
        
        btnInPerson.setOnClickListener(v -> {
            updateSessionType(false);
        });
        
        btnConfirm.setOnClickListener(v -> {
            if (selectedDate != null && selectedTimeSlot != null) {
                showAppointmentConfirmationDialog();
            } else {
                Toast.makeText(getContext(), "Please select a date and time", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAddCalendar.setOnClickListener(v -> {
            if (selectedDate != null && selectedTimeSlot != null) {
                Toast.makeText(getContext(), "Appointment added to calendar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please select a date and time first", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showAppointmentConfirmationDialog() {
        if (getContext() == null) return;
        
        AppointmentConfirmationDialog dialog = new AppointmentConfirmationDialog(
                getContext(),
                therapist,
                selectedDate.getDate(),
                selectedTimeSlot.getTime(),
                new AppointmentConfirmationDialog.OnDialogActionListener() {
                    @Override
                    public void onDoneClicked() {
                        // Return to previous screen
                        getParentFragmentManager().popBackStack();
                    }

                    @Override
                    public void onEditClicked() {
                        // Stay on current screen to edit appointment
                        // Do nothing, dialog will dismiss automatically
                    }
                }
        );
        
        dialog.show();
    }
    
    private void updateSessionType(boolean isOnline) {
        isOnlineSelected = isOnline;
        
        btnOnline.setBackgroundTintList(getResources().getColorStateList(
                isOnline ? R.color.peach : R.color.background));
        btnOnline.setTextColor(getResources().getColor(
                isOnline ? R.color.white : R.color.black));
                
        btnInPerson.setBackgroundTintList(getResources().getColorStateList(
                !isOnline ? R.color.peach : R.color.background));
        btnInPerson.setTextColor(getResources().getColor(
                !isOnline ? R.color.white : R.color.black));
    }
} 