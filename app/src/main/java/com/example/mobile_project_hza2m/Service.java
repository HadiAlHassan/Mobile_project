package com.example.mobile_project_hza2m;

public class Service {
    private int serviceId;
    private String serviceName;
    private String logoUrl;
    private String category;
    private String description;
    private String bankAccount;
    private int categoryId; // ✅ ADD THIS

    // ✅ Updated constructor
    public Service(int serviceId, String serviceName, String logoUrl, String category, String description, String bankAccount, int categoryId) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.logoUrl = logoUrl;
        this.category = category;
        this.description = description;
        this.bankAccount = bankAccount;
        this.categoryId = categoryId;
    }

    // Optionally keep overloads
    public Service(int serviceId, String serviceName, String logoUrl, String category) {
        this(serviceId, serviceName, logoUrl, category, null, null, -1);
    }

    public Service(int serviceId, String serviceName, String logoUrl) {
        this(serviceId, serviceName, logoUrl, null, null, null, -1);
    }

    public int getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getLogoUrl() { return logoUrl; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getBankAccount() { return bankAccount; }
    public int getCategoryId() { return categoryId; } // ✅ Getter
}
