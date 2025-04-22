package com.example.mobile_project_hza2m;

public class ServiceItem {
    private int itemId;
    private String itemName;
    private String itemDescription;
    private String itemPrice;
    private String itemImage;

    public ServiceItem(int itemId, String itemName, String itemDescription, String itemPrice, String itemImage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.itemImage = itemImage;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getItemDescription() { return itemDescription; }
    public String getItemPrice() { return itemPrice; }
    public String getItemImage() { return itemImage; }
}
