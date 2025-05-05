package com.krishna.navbar.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.krishna.navbar.R;
import com.krishna.navbar.models.TherapistBooking;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<TherapistBooking> bookings;
    private Context context;
    private SimpleDateFormat inputDateFormat;
    private SimpleDateFormat outputDateFormat;
    
    public AppointmentAdapter(Context context, List<TherapistBooking> bookings) {
        this.context = context;
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        this.inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.outputDateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        TherapistBooking booking = bookings.get(position);
        
        // Set therapist name and appointment info
        holder.tvTherapistName.setText(booking.getTherapistName());
        
        // Format and set date
        try {
            Date date = inputDateFormat.parse(booking.getDate());
            holder.tvDate.setText(outputDateFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText(booking.getDate());
        }
        
        // Set time and mode
        holder.tvTime.setText(booking.getTime());
        holder.tvMode.setText(booking.getMode());
        
        // Set appropriate icon based on mode
        if (booking.getMode() != null && booking.getMode().toLowerCase().contains("online")) {
            holder.tvMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video_call, 0, 0, 0);
        } else {
            // For in-person sessions, use a different icon or none
            holder.tvMode.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        
        // Set status with appropriate color
        holder.tvStatus.setText(booking.getStatus());
        int statusColor;
        switch (booking.getStatus().toLowerCase()) {
            case "confirmed":
                statusColor = context.getResources().getColor(R.color.green);
                break;
            case "cancelled":
                statusColor = context.getResources().getColor(R.color.red);
                break;
            case "completed":
                statusColor = context.getResources().getColor(R.color.blue);
                break;
            default:
                statusColor = context.getResources().getColor(R.color.black);
                break;
        }
        holder.tvStatus.setTextColor(statusColor);
        
        // Set calendar button status
        updateCalendarButton(holder, booking);
        
        // Set calendar button click listener
        holder.btnAddCalendar.setOnClickListener(v -> {
            addEventToCalendar(booking);
            booking.setAddedToCalendar(true);
            updateCalendarButton(holder, booking);
            
            // Update Firestore
            updateBookingInFirestore(booking);
        });
    }
    
    private void updateCalendarButton(AppointmentViewHolder holder, TherapistBooking booking) {
        if (booking.isAddedToCalendar()) {
            holder.btnAddCalendar.setText("Added to Calendar");
            holder.btnAddCalendar.setEnabled(false);
            holder.btnAddCalendar.setAlpha(0.5f);
        } else {
            holder.btnAddCalendar.setText("Add to Calendar");
            holder.btnAddCalendar.setEnabled(true);
            holder.btnAddCalendar.setAlpha(1.0f);
        }
    }
    
    private void addEventToCalendar(TherapistBooking booking) {
        try {
            // Parse date and time
            Date appointmentDate = inputDateFormat.parse(booking.getDate());
            
            // Create calendar instance
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(appointmentDate);
            
            // Parse time (assuming format like "10:00 AM")
            String[] timeParts = booking.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1].split(" ")[0]);
            boolean isPM = booking.getTime().toUpperCase().contains("PM");
            
            if (isPM && hour < 12) {
                hour += 12;
            } else if (!isPM && hour == 12) {
                hour = 0;
            }
            
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            
            // Create intent to add calendar event
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "Therapy Session with " + booking.getTherapistName())
                    .putExtra(CalendarContract.Events.DESCRIPTION, booking.getMode() + " therapy session")
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis() + 3600000) // 1 hour session
                    .putExtra(CalendarContract.Events.ALL_DAY, false)
                    .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            
            context.startActivity(intent);
            
            Toast.makeText(context, "Adding to calendar...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to add to calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateBookingInFirestore(TherapistBooking booking) {
        FirestoreHelper helper = new FirestoreHelper();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        helper.updateBookingCalendarStatus(userId, booking.getId(), booking.isAddedToCalendar())
            .addOnSuccessListener(aVoid -> {
                // Successfully updated
            })
            .addOnFailureListener(e -> {
                // Failed to update, revert UI
                booking.setAddedToCalendar(false);
                notifyDataSetChanged();
                Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }
    
    public void updateList(List<TherapistBooking> newBookings) {
        this.bookings = newBookings != null ? newBookings : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTherapistName, tvDate, tvTime, tvMode, tvStatus;
        Button btnAddCalendar;
        
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvTherapistName = itemView.findViewById(R.id.tv_therapist_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvMode = itemView.findViewById(R.id.tv_mode);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAddCalendar = itemView.findViewById(R.id.btn_add_calendar);
        }
    }
} 