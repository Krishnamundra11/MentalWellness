package com.krishna.navbar.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a questionnaire response to be saved in Firestore
 */
public class QuestionnaireResponse {
    private String category;
    private String date;
    private int score;
    private Map<String, String> answers;

    // Empty constructor required for Firestore
    public QuestionnaireResponse() {
    }

    // Constructor with all fields
    public QuestionnaireResponse(String category, String date, int score, Map<String, String> answers) {
        this.category = category;
        this.date = date;
        this.score = score;
        this.answers = answers;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    // Convert int[] answers to a Map for Firestore
    public static Map<String, String> convertAnswersToMap(int[] answers) {
        Map<String, String> answerMap = new HashMap<>();
        for (int i = 0; i < answers.length; i++) {
            String questionKey = "q" + (i + 1);
            String answerValue = getAnswerText(answers[i]);
            answerMap.put(questionKey, answerValue);
        }
        return answerMap;
    }

    // Convert numeric answer to text representation
    private static String getAnswerText(int answerValue) {
        switch (answerValue) {
            case 1:
                return "Very Poor";
            case 2:
                return "Poor";
            case 3:
                return "Okay";
            case 4:
                return "Good";
            case 5:
                return "Excellent";
            default:
                return "Unknown";
        }
    }
} 