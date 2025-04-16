package com.example.mobile_project_hza2m;

public class Company {
    private String name;
    private int iconResId;

    public Company(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
