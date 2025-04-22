package com.example.mobile_project_hza2m;


public class Service {
    private int serviceId;
    private String serviceName;
    private String logoUrl;
    private String category;      // optional, can be null for provider view
    private String description;   // optional
    private String bankAccount;   // optional


    public Service(int serviceId, String serviceName, String logoUrl, String category, String description, String bankAccount) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.logoUrl = logoUrl;
        this.category = category;
        this.description = description;
        this.bankAccount = bankAccount;
    }

    public Service(int serviceId, String serviceName, String logoUrl) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.logoUrl = logoUrl;
    }

    public Service(int serviceId, String serviceName, String logoUrl, String category) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.logoUrl = logoUrl;
        this.category = category;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getBankAccount() {
        return bankAccount;
    }
}
