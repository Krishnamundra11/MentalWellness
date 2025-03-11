package com.krishna.navbar.models;

public class Therapist {
    private String name, experience, expertise, languages, profileImage, sessionType, availability, nextSlot;
    private double price;

    // Constructor (empty for Firebase compatibility)
    public Therapist() {}

    public Therapist(String name, String experience, String expertise, String languages,
                     String profileImage, String sessionType, String availability,
                     String nextSlot, double price) {
        this.name = name;
        this.experience = experience;
        this.expertise = expertise;
        this.languages = languages;
        this.profileImage = profileImage;
        this.sessionType = sessionType;
        this.availability = availability;
        this.nextSlot = nextSlot;
        this.price = price;
    }

    // Getters & Setters
    public String getName() { return name; }
    public String getExperience() { return experience; }
    public String getExpertise() { return expertise; }
    public String getLanguages() { return languages; }
    public String getProfileImage() { return profileImage; }
    public String getSessionType() { return sessionType; }
    public String getAvailability() { return availability; }
    public String getNextSlot() { return nextSlot; }
    public double getPrice() { return price; }
}
