package com.krishna.navbar.models;

public class UserProfile {
    private String name;
    private int age;
    private String gender;
    private String email;
    private String profession;
    private String profileImageURL;

    // Empty constructor required for Firestore
    public UserProfile() {
    }

    // Full constructor
    public UserProfile(String name, int age, String gender, String email, String profession, String profileImageURL) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.profession = profession;
        this.profileImageURL = profileImageURL;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
} 