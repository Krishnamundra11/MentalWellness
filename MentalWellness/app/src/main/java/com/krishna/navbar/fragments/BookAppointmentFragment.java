package com.krishna.navbar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import com.google.firebase.auth.FirebaseAuth;
import com.krishna.navbar.R;
import com.krishna.navbar.adapters.DateAdapter;
import com.krishna.navbar.adapters.TimeSlotAdapter;
import com.krishna.navbar.models.DateItem;
import com.krishna.navbar.models.Therapist;
import com.krishna.navbar.models.TimeSlot;
import com.krishna.navbar.models.TherapistBooking;
import com.krishna.navbar.utils.FirestoreHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.bumptech.glide.Glide;
import com.krishna.navbar.utils.NavigationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private String therapistId;
    private List<DateItem> dateItems;
    private List<TimeSlot> timeSlots;

    public static BookAppointmentFragment newInstance(String therapistId) {
        BookAppointmentFragment fragment = new BookAppointmentFragment();
        Bundle args = new Bundle();
        args.putString("therapistId", therapistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);
        
        // Hide bottom navigation when this fragment is shown
        if (getActivity() != null) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        }
        
        extractArguments();
        initViews(view);
        fetchTherapistFromFirestore();
        setupListeners();
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

    private void extractArguments() {
        if (getArguments() != null) {
            therapistId = getArguments().getString("therapistId");
        }
    }

    private void fetchTherapistFromFirestore() {
        if (therapistId == null) {
            Toast.makeText(getContext(), "Therapist not found", Toast.LENGTH_SHORT).show();
            return;
        }
        FirestoreHelper helper = new FirestoreHelper();
        helper.getTherapistById(therapistId)
            .addOnSuccessListener(documentSnapshot -> {
                therapist = documentSnapshot.toObject(Therapist.class);
                if (therapist != null) {
                    therapist.setId(documentSnapshot.getId());
                    setupData();
                } else {
                    Toast.makeText(getContext(), "Therapist not found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load therapist", Toast.LENGTH_SHORT).show());
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        ivTherapist = view.findViewById(R.id.iv_therapist);
        tvTherapistName = view.findViewById(R.id.tv_therapist_name);
        tvTherapistSpecialty = view.findViewById(R.id.tv_therapist_specialty);
        tvTherapistExperience = view.findViewById(R.id.tv_therapist_experience);
        btnOnline = view.findViewById(R.id.btn_online);
        btnInPerson = view.findViewById(R.id.btn_in_person);
        rvDates = view.findViewById(R.id.rv_dates);
        rvTimeSlots = view.findViewById(R.id.rv_time_slots);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnAddCalendar = view.findViewById(R.id.btn_add_calendar);
        
        // Initialize lists to prevent null pointer exceptions
        dateItems = new ArrayList<>();
        timeSlots = new ArrayList<>();
    }

    private void setupData() {
        if (therapist == null) return;
        tvTherapistName.setText(therapist.getName());
        tvTherapistSpecialty.setText(therapist.getSpecialization() != null ? therapist.getSpecialization().toString() : "");
        tvTherapistExperience.setText(therapist.getExperience());
        
        if (therapist.getProfileImageUrl() != null && !therapist.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(therapist.getProfileImageUrl()).into(ivTherapist);
        } else {
            ivTherapist.setImageResource(R.drawable.placeholder_therapist);
        }
        
        updateSessionType(isOnlineSelected);
        setupDateRecyclerView();
    }

    private void setupDateRecyclerView() {
        dateItems = generateNextFiveDays();
        dateAdapter = new DateAdapter(dateItems);
        dateAdapter.setOnDateSelectedListener(dateItem -> {
            selectedDate = dateItem;
            fetchAvailableSlotsForDate();
        });
        rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDates.setAdapter(dateAdapter);
        if (!dateItems.isEmpty()) {
            selectedDate = dateItems.get(0);
            dateAdapter.selectDate(0);
            fetchAvailableSlotsForDate();
        }
    }

    private void fetchAvailableSlotsForDate() {
        if (therapist == null || selectedDate == null) return;
        FirestoreHelper helper = new FirestoreHelper();
        helper.getAvailableSlots(therapist.getId())
            .addOnSuccessListener(documentSnapshot -> {
                Therapist t = documentSnapshot.toObject(Therapist.class);
                if (t != null && t.getAvailableSlots() != null) {
                    String dateKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getDate());
                    List<String> slots = t.getAvailableSlots().get(dateKey);
                    if (slots != null && !slots.isEmpty()) {
                        timeSlots = new ArrayList<>();
                        for (String slot : slots) {
                            timeSlots.add(new TimeSlot(slot));
                        }
                    } else {
                        timeSlots = new ArrayList<>();
                    }
                    setupTimeSlotRecyclerView();
                }
            });
    }

    private void setupTimeSlotRecyclerView() {
        if (timeSlots == null) timeSlots = new ArrayList<>();
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

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavigationHelper.handleBackNavigation(this));
        btnOnline.setOnClickListener(v -> updateSessionType(true));
        btnInPerson.setOnClickListener(v -> updateSessionType(false));
        btnConfirm.setOnClickListener(v -> {
            if (selectedDate != null && selectedTimeSlot != null && therapist != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getDate());
                String mode = isOnlineSelected ? "Online" : "In-Person";
                TherapistBooking booking = new TherapistBooking(
                        therapist.getId(), therapist.getName(), dateStr, selectedTimeSlot.getTime(), mode, "Confirmed");
                FirestoreHelper helper = new FirestoreHelper();
                helper.bookAppointment(userId, booking, therapist.getId(), dateStr, selectedTimeSlot.getTime(),
                        aVoid -> {
                            BookingConfirmationFragment confirmationFragment = BookingConfirmationFragment.newInstance(
                                    therapist, selectedDate.getDate(), selectedTimeSlot.getTime(), isOnlineSelected);
                            NavigationHelper.navigateToFragment(this, confirmationFragment, true);
                        },
                        e -> Toast.makeText(getContext(), "Failed to book appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Please select a date and time", Toast.LENGTH_SHORT).show();
            }
        });
        btnAddCalendar.setOnClickListener(v -> addEventToCalendar());
    }

    private void addEventToCalendar() {
        try {
            // Create calendar intent
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            
            // Set calendar event details
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate.getDate());
            
            // Parse time (assuming format like "10:00 AM")
            String[] timeParts = selectedTimeSlot.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1].split(" ")[0]);
            boolean isPM = selectedTimeSlot.getTime().toUpperCase().contains("PM");
            
            if (isPM && hour < 12) {
                hour += 12;
            } else if (!isPM && hour == 12) {
                hour = 0;
            }
            
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
            
            // Set duration to 1 hour
            cal.add(Calendar.HOUR, 1);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis());
            
            intent.putExtra(CalendarContract.Events.TITLE, "Therapy Session with " + therapist.getName());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, (isOnlineSelected ? "Online" : "In-Person") + " therapy session");
            
            startActivity(intent);
            Toast.makeText(getContext(), "Adding to calendar...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to add to calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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