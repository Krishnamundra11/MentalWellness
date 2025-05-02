package com.krishna.navbar.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.models.QuestionnaireResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for Firestore operations
 */
public class FirestoreHelper {
    
    private static final String USERS_COLLECTION = "users";
    private static final String QUESTIONNAIRES_COLLECTION = "questionnaires";
    
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
} 