package com.krishna.navbar.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.krishna.navbar.R;
import com.krishna.navbar.models.Therapist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentConfirmationDialog extends Dialog {

    private final Therapist therapist;
    private final Date appointmentDate;
    private final String appointmentTime;
    private final OnDialogActionListener listener;

    public interface OnDialogActionListener {
        void onDoneClicked();
        void onEditClicked();
    }

    public AppointmentConfirmationDialog(@NonNull Context context, Therapist therapist, 
                                         Date appointmentDate, String appointmentTime,
                                         OnDialogActionListener listener) {
        super(context);
        this.therapist = therapist;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_appointment_confirmation);

        // Set dialog window attributes
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setCancelable(false);

        // Initialize views
        TextView tvAppointmentDetails = findViewById(R.id.tvAppointmentDetails);
        Button btnDone = findViewById(R.id.btnDone);
        TextView tvEditAppointment = findViewById(R.id.tvEditAppointment);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d", Locale.getDefault());
        String formattedDate = dateFormat.format(appointmentDate);

        // Set appointment details
        String details = String.format("You booked an appointment with %s on %s, at %s", 
                therapist.getName(), formattedDate, appointmentTime);
        tvAppointmentDetails.setText(details);

        // Set click listeners
        btnDone.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoneClicked();
            }
            dismiss();
        });

        tvEditAppointment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClicked();
            }
            dismiss();
        });
    }
} 