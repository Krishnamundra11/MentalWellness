package com.krishna.navbar.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {
    private static final String TAG = "UserUtils";
    private static final String USERS_COLLECTION = "users";
    private static final String FIELD_NAME = "name";
    
    private static UserUtils instance;
    private final FirebaseFirestore db;
    private final MutableLiveData<String> userNameLiveData;
    private String cachedUserName;
    
    private UserUtils() {
        db = FirebaseFirestore.getInstance();
        userNameLiveData = new MutableLiveData<>();
    }
    
    public static synchronized UserUtils getInstance() {
        if (instance == null) {
            instance = new UserUtils();
        }
        return instance;
    }
    
    public interface OnNameFetched {
        void onNameFetched(String name);
        void onError(String error);
    }
    
    public LiveData<String> getUserNameLiveData() {
        return userNameLiveData;
    }
    
    public void getUserName(Context context, OnNameFetched callback) {
        // Return cached name if available
        if (cachedUserName != null) {
            callback.onNameFetched(cachedUserName);
            return;
        }
        
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onError("No user logged in");
            return;
        }
        
        db.collection(USERS_COLLECTION)
            .document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FIELD_NAME);
                    if (name != null && !name.isEmpty()) {
                        cachedUserName = name;
                        userNameLiveData.postValue(name);
                        callback.onNameFetched(name);
                    } else {
                        // Fallback to email username if name not set
                        String email = currentUser.getEmail();
                        if (email != null) {
                            String[] parts = email.split("@");
                            String username = parts.length > 0 ? parts[0] : "";
                            if (!username.isEmpty()) {
                                username = username.substring(0, 1).toUpperCase() + username.substring(1);
                                cachedUserName = username;
                                userNameLiveData.postValue(username);
                                callback.onNameFetched(username);
                            } else {
                                callback.onError("Could not determine user name");
                            }
                        } else {
                            callback.onError("Could not determine user name");
                        }
                    }
                } else {
                    // Create user document if it doesn't exist
                    createUserDocument(currentUser, callback);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching user name", e);
                callback.onError("Error fetching user name: " + e.getMessage());
            });
    }
    
    private void createUserDocument(FirebaseUser user, OnNameFetched callback) {
        String email = user.getEmail();
        String[] parts = email != null ? email.split("@") : new String[0];
        String username = parts.length > 0 ? parts[0] : "";
        if (!username.isEmpty()) {
            username = username.substring(0, 1).toUpperCase() + username.substring(1);
        }
        
        // Make username final for use in lambda
        final String finalUsername = username;
        
        db.collection(USERS_COLLECTION)
            .document(user.getUid())
            .set(new java.util.HashMap<String, Object>() {{
                put(FIELD_NAME, finalUsername);
                put("email", email);
                put("createdAt", com.google.firebase.Timestamp.now());
            }})
            .addOnSuccessListener(aVoid -> {
                cachedUserName = finalUsername;
                userNameLiveData.postValue(finalUsername);
                callback.onNameFetched(finalUsername);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error creating user document", e);
                callback.onError("Error creating user document: " + e.getMessage());
            });
    }
    
    public void clearCache() {
        cachedUserName = null;
        userNameLiveData.postValue(null);
    }
} 