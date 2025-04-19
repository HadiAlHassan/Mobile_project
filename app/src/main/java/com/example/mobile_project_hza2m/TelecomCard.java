package com.example.mobile_project_hza2m;

public class TelecomCard {
    private String value;
    private int imageResId;

    public TelecomCard(String value, int imageResId) {
        this.value = value;
        this.imageResId = imageResId;
    }

    public String getValue() {
        return value;
    }

    public int getImageResId() {
        return imageResId;
    }
}

