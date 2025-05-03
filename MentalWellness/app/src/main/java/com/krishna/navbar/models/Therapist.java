package com.krishna.navbar.models;

import java.util.List;
import java.util.Map;

public class Therapist {
    private String id;
    private String name;
    private List<String> specialization;
    private List<String> languages;
    private String experience;
    private int fee;
    private List<String> modes;
    private float rating;
    private int reviewCount;
    private Map<String, List<String>> availableSlots; // date -> slots
    private String profileImageUrl;
    private String aboutMe;

    public Therapist() {}

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getSpecialization() { return specialization; }
    public void setSpecialization(List<String> specialization) { this.specialization = specialization; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public int getFee() { return fee; }
    public void setFee(int fee) { this.fee = fee; }
    public List<String> getModes() { return modes; }
    public void setModes(List<String> modes) { this.modes = modes; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public Map<String, List<String>> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Map<String, List<String>> availableSlots) { this.availableSlots = availableSlots; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }
}
