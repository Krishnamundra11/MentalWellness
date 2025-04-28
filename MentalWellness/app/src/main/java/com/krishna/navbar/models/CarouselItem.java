package com.krishna.navbar.models;

/**
 * Model class for items in the landing page carousel
 */
public class CarouselItem {
    private String title;
    private String subtitle;
    private int imageResourceId;
    private int backgroundColor;

    public CarouselItem(String title, String subtitle, int imageResourceId, int backgroundColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageResourceId = imageResourceId;
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
} 