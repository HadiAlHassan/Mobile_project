package com.example.mobile_project_hza2m;

public class TelecomPlan {
    private int itemId;
    private int serviceId;
    private String title;
    private String description;
    private String price;
    private String imageUrl; // 🔄 Firebase-hosted image URL

    public TelecomPlan(int itemId, int serviceId, String title, String description, String price, String imageUrl) {
        this.itemId = itemId;
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getItemId() { return itemId; }
    public int getServiceId() { return serviceId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
