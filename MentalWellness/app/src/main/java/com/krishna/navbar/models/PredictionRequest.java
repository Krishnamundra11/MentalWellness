package com.krishna.navbar.models;

import java.util.Map;

/**
 * Model class representing a prediction request to be sent to the API
 */
public class PredictionRequest {
    private Map<String, String> answers;

    // Empty constructor
    public PredictionRequest() {
    }

    // Constructor
    public PredictionRequest(Map<String, String> answers) {
        this.answers = answers;
    }

    // Getters and Setters
    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
} 