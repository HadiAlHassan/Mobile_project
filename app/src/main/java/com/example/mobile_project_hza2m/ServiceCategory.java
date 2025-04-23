package com.example.mobile_project_hza2m;

import java.util.List;

public class ServiceCategory {
    private String categoryName;
    private List<Service> services;

    public ServiceCategory(String categoryName, List<Service> services) {
        this.categoryName = categoryName;
        this.services = services;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Service> getServices() {
        return services;
    }
}
