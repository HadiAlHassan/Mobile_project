package com.example.mobile_project_hza2m;

public class ServiceItem {
    private int id;
    private String name;
    private String description;
    private String price;

    public ServiceItem(int id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
}
