package com.example.mobile_project_hza2m;

public class StreamingPlan {
    private int itemId;
    private int serviceId;
    private String name;
    private String description;
    private String price;
    private int imageResId; // ✅ drawable resource ID

    public StreamingPlan(int itemId, int serviceId, String name, String description, String price, int imageResId) {
        this.itemId = itemId;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
    }

    public int getItemId() { return itemId; }
    public int getServiceId() { return serviceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
