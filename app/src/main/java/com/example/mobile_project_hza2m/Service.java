package com.example.mobile_project_hza2m;

public class Service {
    private String name;
    private String price;
    private int iconResId;

    public Service(String name, String price, int iconResId) {
        this.name = name;
        this.price = price;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getIconResId() {
        return iconResId;
    }
}
