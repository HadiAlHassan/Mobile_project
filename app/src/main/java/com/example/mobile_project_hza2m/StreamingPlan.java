package com.example.mobile_project_hza2m;



public class StreamingPlan {
    private int itemId;
    private String name;
    private String description;
    private String price;
    private int imageRes;

    public StreamingPlan(int itemId, String name, String description, String price, int imageRes) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageRes = imageRes;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public int getImageRes() { return imageRes; }
}
