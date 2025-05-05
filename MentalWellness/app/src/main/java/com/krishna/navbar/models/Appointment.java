package com.krishna.navbar.models;

/**
 * Appointment is just an alias for TherapistBooking to maintain compatibility 
 * while migrating the codebase.
 */
public class Appointment extends TherapistBooking {
    
    public Appointment() {
        super();
    }
    
    public Appointment(String therapistId, String therapistName, 
                       String date, String time, String mode, String status) {
        super(therapistId, therapistName, date, time, mode, status);
    }
    
    // All getters and setters are inherited from TherapistBooking
} 