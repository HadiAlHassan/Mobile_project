package com.example.mobile_project_hza2m;

import java.util.List;

public class ServiceCategory {
    private String categoryName;
    private List<Company> companies;
    private String serviceType; // ✅ New field

    public ServiceCategory(String categoryName, List<Company> companies) {
        this.categoryName = categoryName;
        this.companies = companies;
        this.serviceType = categoryName; // default to categoryName
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
