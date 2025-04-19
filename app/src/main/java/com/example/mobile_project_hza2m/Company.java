package com.example.mobile_project_hza2m;


public class Company {
    private String name;
    private int iconResId;
    private String serviceType; // ✅ New field

    public Company(String name, int iconResId, String serviceType) {
        this.name = name;
        this.iconResId = iconResId;
        this.serviceType = serviceType;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}