package com.example.mobile_project_hza2m;

public class TelecomCard {
    private int itemId;
    private int serviceId;
    private String title;
    private String description;
    private String price;
    private int imageResId;

    public TelecomCard(int itemId, int serviceId, String title, String description, String price, int imageResId) {
        this.itemId = itemId;
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
