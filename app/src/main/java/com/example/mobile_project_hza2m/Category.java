package com.example.mobile_project_hza2m;


import java.util.List;

public class Category {
    private String name;
    private List<Service> services;

    public Category(String name, List<Service> services) {
        this.name = name;
        this.services = services;
    }

    public String getName() {
        return name;
    }

    public List<Service> getServices() {
        return services;
    }
}
