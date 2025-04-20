package com.example.mobile_project_hza2m;

public class Provider {
    private int providerId;
    private String businessName;

    public Provider(int providerId, String businessName) {
        this.providerId = providerId;
        this.businessName = businessName;
    }

    public int getProviderId() {
        return providerId;
    }

    public String getBusinessName() {
        return businessName;
    }
}
