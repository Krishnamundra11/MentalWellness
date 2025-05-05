package com.krishna.navbar.utils;

import java.util.HashMap;
import java.util.Map;

public class ScoreUtils {

    // Convert answer text to numeric value
    public static int getAnswerValue(String answerText) {
        switch (answerText) {
            case "Never":
                return 0;
            case "Almost Never":
                return 1;
            case "Sometimes":
                return 2;
            case "Fairly Often":
                return 3;
            case "Very Often":
                return 4;
            default:
                return 0;
        }
    }

    // Convert numeric value to answer text
    public static String getAnswerText(int value) {
        switch (value) {
            case 0:
                return "Never";
            case 1:
                return "Almost Never";
            case 2:
                return "Sometimes";
            case 3:
                return "Fairly Often";
            case 4:
                return "Very Often";
            default:
                return "Never";
        }
    }

    // Calculate total score from answers array
    public static int calculateScore(int[] answers) {
        int totalScore = 0;
        for (int answer : answers) {
            totalScore += answer;
        }
        return totalScore;
    }

    // Calculate percentage score (0-100) from total score
    public static int calculatePercentageScore(int totalScore, int totalQuestions) {
        return totalScore * 100 / (totalQuestions * 4); // 4 is max value per question
    }

    // Convert answers array to map for API request
    public static Map<String, String> convertAnswersToMap(int[] answers) {
        Map<String, String> answerMap = new HashMap<>();
        for (int i = 0; i < answers.length; i++) {
            String questionKey = "q" + (i + 1);
            String answerValue = getAnswerText(answers[i]);
            answerMap.put(questionKey, answerValue);
        }
        return answerMap;
    }
} 