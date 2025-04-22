package com.example.mobile_project_hza2m;
public class Services {
    private int id;
    private String name;
    private String price;
    private int iconResId;

    public Services(int id, String name, String price, int iconResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
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

