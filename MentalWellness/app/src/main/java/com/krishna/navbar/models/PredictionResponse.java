package com.krishna.navbar.models;

/**
 * Model class representing a prediction response received from the API
 */
public class PredictionResponse {
    private String prediction;
    private String recommendation;

    // Empty constructor
    public PredictionResponse() {
    }

    // Constructor
    public PredictionResponse(String prediction, String recommendation) {
        this.prediction = prediction;
        this.recommendation = recommendation;
    }

    // Getters and Setters
    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
} 