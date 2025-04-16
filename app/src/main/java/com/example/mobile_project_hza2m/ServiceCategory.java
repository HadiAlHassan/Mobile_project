package com.example.mobile_project_hza2m;

import java.util.List;

public class ServiceCategory {
    private String categoryName;
    private List<Company> companies;

    public ServiceCategory(String categoryName, List<Company> companies) {
        this.categoryName = categoryName;
        this.companies = companies;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<Company> getCompanies() {
        return companies;
    }
}
