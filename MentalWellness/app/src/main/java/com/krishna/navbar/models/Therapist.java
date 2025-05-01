package com.krishna.navbar.models;

public class Therapist {
    private String name;
    private String specialization;
    private float rating;
    private int reviewCount;
    private String experience;
    private String languages;
    private String nextAvailable;
    private int profileImageResourceId;
    private boolean isFavorite;

    public Therapist(String name, String specialization, float rating, int reviewCount, 
                     String experience, String languages, String nextAvailable, 
                     int profileImageResourceId) {
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.experience = experience;
        this.languages = languages;
        this.nextAvailable = nextAvailable;
        this.profileImageResourceId = profileImageResourceId;
        this.isFavorite = false;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getExperience() {
        return experience;
    }

    public String getLanguages() {
        return languages;
    }

    public String getNextAvailable() {
        return nextAvailable;
    }

    public int getProfileImageResourceId() {
        return profileImageResourceId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void toggleFavorite() {
        isFavorite = !isFavorite;
    }
}
