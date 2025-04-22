package com.example.mobile_project_hza2m;


public class Company {
    private String name;
    private int drawableId;
    private String category;
    private int serviceId; // ✅ Add this

    public Company(String name, int drawableId, String category, int serviceId) {
        this.name = name;
        this.drawableId = drawableId;
        this.category = category;
        this.serviceId = serviceId;
    }

    public String getName() { return name; }
    public int getDrawableId() { return drawableId; }
    public String getCategory() { return category; }
    public int getServiceId() { return serviceId; }
}
