package com.example.mobile_project_hza2m;

public class SubscriptionPlan {
    private String title;
    private String price;
    private int maxUsers;

    private int imageResId; // New field

    public SubscriptionPlan(String title, String price, int maxUsers, int imageResId) {
        this.title = title;
        this.price = price;
        this.maxUsers = maxUsers;
        this.imageResId = imageResId;
    }

    public int getImageResId() {
        return imageResId;
    }


    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getMaxUsers() {
        return maxUsers;
    }
}
