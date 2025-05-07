package com.krishna.navbar.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.models.QuestionnaireResponse;
import com.krishna.navbar.models.Therapist;
import com.krishna.navbar.models.TherapistBooking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for Firestore operations
 */
public class FirestoreHelper {
    
    private static final String USERS_COLLECTION = "users";
    private static final String QUESTIONNAIRES_COLLECTION = "questionnaires";
    private static final String THERAPISTS_COLLECTION = "therapists";
    private static final String BOOKINGS_SUBCOLLECTION = "therapist_bookings";
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    
    /**
     * Get all questionnaire responses for the current user
     * @return Task with QuerySnapshot containing responses
     */
    public Task<QuerySnapshot> getAllResponses() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        return db.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .collection(QUESTIONNAIRES_COLLECTION)
                .get();
    }
    
    /**
     * Get questionnaire responses for a specific category (e.g., "stress", "academic", "sleep")
     * @param category The category to filter by
     * @return Task with QuerySnapshot containing filtered responses
     */
    public Task<QuerySnapshot> getResponsesByCategory(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        return db.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .collection(QUESTIONNAIRES_COLLECTION)
                .whereEqualTo("category", category)
                .get();
    }
    
    /**
     * Get the most recent questionnaire response for a specific category
     * @param category The category to filter by
     * @return Task with QuerySnapshot containing the most recent response
     */
    public Task<QuerySnapshot> getMostRecentResponseByCategory(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        return db.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .collection(QUESTIONNAIRES_COLLECTION)
                .whereEqualTo("category", category)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get();
    }
    
    /**
     * Check if the current user has already completed a questionnaire for the specific category today
     * @param category The questionnaire category to check
     * @return Task with DocumentSnapshot to check if document exists
     */
    public Task<DocumentSnapshot> hasCompletedQuestionnaireToday(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        // Get current date in yyyy-MM-dd format
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Document ID format: category-date (e.g., stress-2023-05-01)
        String documentId = category + "-" + currentDate;
        
        return db.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .collection(QUESTIONNAIRES_COLLECTION)
                .document(documentId)
                .get();
    }
    
    /**
     * Get a reference to today's questionnaire document for a specific category
     * @param category The questionnaire category
     * @return DocumentReference for today's questionnaire
     */
    public DocumentReference getTodayQuestionnaireReference(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        // Get current date in yyyy-MM-dd format
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Document ID format: category-date (e.g., stress-2023-05-01)
        String documentId = category + "-" + currentDate;
        
        return db.collection(USERS_COLLECTION)
                .document(currentUser.getUid())
                .collection(QUESTIONNAIRES_COLLECTION)
                .document(documentId);
    }
    
    /**
     * Get questionnaire responses for a specific category within a date range
     * @param category The category to filter by
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return Task with QuerySnapshot containing filtered responses
     */
    public Task<QuerySnapshot> getResponsesByDateRange(String category, String startDate, String endDate) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        
        // This query requires a composite index (added in firestore.indexes.json)
        // See https://firebase.google.com/docs/firestore/query-data/indexing
        try {
            return db.collection(USERS_COLLECTION)
                    .document(currentUser.getUid())
                    .collection(QUESTIONNAIRES_COLLECTION)
                    .whereEqualTo("category", category)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate)
                    .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .get();
        } catch (Exception e) {
            // If the query fails due to missing index, use a workaround:
            // First get all documents by category, then filter by date in the app
            System.out.println("DEBUG: Using fallback query method due to index issue: " + e.getMessage());
            return db.collection(USERS_COLLECTION)
                    .document(currentUser.getUid())
                    .collection(QUESTIONNAIRES_COLLECTION)
                    .whereEqualTo("category", category)
                    .get();
        }
    }

    // Fetch all therapists with optional filters
    public Task<QuerySnapshot> getTherapists(String specialization, String language, String mode) {
        CollectionReference ref = db.collection(THERAPISTS_COLLECTION);
        Query query = ref;
        if (specialization != null && !specialization.isEmpty())
            query = query.whereArrayContains("specialization", specialization);
        if (language != null && !language.isEmpty())
            query = query.whereArrayContains("languages", language);
        if (mode != null && !mode.isEmpty())
            query = query.whereArrayContains("modes", mode);
        return query.get();
    }

    // Fetch therapist details by ID
    public Task<DocumentSnapshot> getTherapistById(String therapistId) {
        return db.collection(THERAPISTS_COLLECTION).document(therapistId).get()
            .addOnFailureListener(e -> {
                if (e instanceof com.google.firebase.firestore.FirebaseFirestoreException) {
                    if (e.getMessage() != null && e.getMessage().contains("DEADLINE_EXCEEDED")) {
                        // Log timeout errors specifically
                        android.util.Log.e("FirestoreHelper", "Timeout while fetching therapist: " + e.getMessage());
                    }
                }
            });
    }

    // Fetch available slots for a therapist (returns the whole doc, slots are in availableSlots)
    public Task<DocumentSnapshot> getAvailableSlots(String therapistId) {
        return db.collection(THERAPISTS_COLLECTION).document(therapistId).get();
    }

    // Book an appointment: add to user subcollection, remove slot from therapist
    public void bookAppointment(String userId, TherapistBooking booking, String therapistId, String date, String time, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(USERS_COLLECTION)
            .document(userId)
            .collection(BOOKINGS_SUBCOLLECTION)
            .add(booking)
            .addOnSuccessListener(documentReference -> {
                DocumentReference therapistRef = db.collection(THERAPISTS_COLLECTION).document(therapistId);
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(therapistRef);
                    Map<String, Object> data = snapshot.getData();
                    if (data == null) return null;
                    Map<String, List<String>> availableSlots = (Map<String, List<String>>) data.get("availableSlots");
                    if (availableSlots != null && availableSlots.containsKey(date)) {
                        List<String> slots = availableSlots.get(date);
                        slots.remove(time);
                        availableSlots.put(date, slots);
                        transaction.update(therapistRef, "availableSlots", availableSlots);
                    }
                    return null;
                }).addOnSuccessListener(result -> onSuccess.onSuccess(null))
                  .addOnFailureListener(onFailure);
            })
            .addOnFailureListener(onFailure);
    }

    // Fetch all appointments for a user
    public Task<QuerySnapshot> getUserBookings(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKINGS_SUBCOLLECTION)
                .get();
    }
    
    // Update the calendar status of a booking
    public Task<Void> updateBookingCalendarStatus(String userId, String bookingId, boolean addedToCalendar) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(BOOKINGS_SUBCOLLECTION)
                .document(bookingId)
                .update("addedToCalendar", addedToCalendar);
    }

    // Get all therapists without any filters
    public Task<QuerySnapshot> getAllTherapists() {
        return db.collection(THERAPISTS_COLLECTION).get();
    }

    // Add a new therapist
    public Task<DocumentReference> addTherapist(Therapist therapist) {
        return db.collection(THERAPISTS_COLLECTION).add(therapist);
    }

    // Delete a therapist
    public Task<Void> deleteTherapist(String therapistId) {
        return db.collection(THERAPISTS_COLLECTION).document(therapistId).delete();
    }
} 