package com.krishna.navbar.models;

public class UserPlaylist {
    private String name;
    private String colorTheme;
    private int trackCount;

    public UserPlaylist(String name, String colorTheme) {
        this.name = name;
        this.colorTheme = colorTheme;
        this.trackCount = 0; // Default to 0 tracks
    }

    public UserPlaylist(String name, String colorTheme, int trackCount) {
        this.name = name;
        this.colorTheme = colorTheme;
        this.trackCount = trackCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }
    
    // Get the resource ID for the background drawable based on the color theme
    public int getBackgroundResourceId() {
        switch (colorTheme) {
            case "blue":
                return com.krishna.navbar.R.drawable.gradient_blue_background;
            case "yellow":
                return com.krishna.navbar.R.drawable.gradient_yellow_background;
            case "purple":
                return com.krishna.navbar.R.drawable.gradient_purple_background;
            case "green":
                return com.krishna.navbar.R.drawable.gradient_green_background;
            case "red":
                return com.krishna.navbar.R.drawable.gradient_red_background;
            case "teal":
                return com.krishna.navbar.R.drawable.gradient_teal_background;
            default:
                return com.krishna.navbar.R.drawable.gradient_blue_background;
        }
    }
    
    // Get the color resource ID for the play button tint based on the color theme
    public int getColorTint() {
        switch (colorTheme) {
            case "blue":
                return com.krishna.navbar.R.color.colorBlue;
            case "yellow":
                return com.krishna.navbar.R.color.colorYellow;
            case "purple":
                return com.krishna.navbar.R.color.colorPurple;
            case "green":
                return com.krishna.navbar.R.color.colorGreen;
            case "red":
                return com.krishna.navbar.R.color.colorRed;
            case "teal":
                return com.krishna.navbar.R.color.colorTeal;
            default:
                return com.krishna.navbar.R.color.colorBlue;
        }
    }
} 