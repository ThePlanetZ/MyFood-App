package com.example.myfood;

public class Order {
    private String dishName;
    private String dishDescription;
    private String dishPrice;
    private String imageUrl;
    private String chefUID;
    private String customerUID;
    private String status;
    private String orderId;
    private String deliveryUID;

    public String getDeliveryUID() {
        return deliveryUID;
    }

    public void setDeliveryUID(String deliveryUID) {
        this.deliveryUID = deliveryUID;
    }

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String orderId,String dishName, String dishDescription, String dishPrice, String imageUrl, String chefUID, String customerUID, String status) {
        this.orderId=orderId;
        this.dishName = dishName;
        this.dishDescription = dishDescription;
        this.dishPrice = dishPrice;
        this.imageUrl = imageUrl;
        this.chefUID = chefUID;
        this.customerUID = customerUID;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    // Getters and setters
    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishDescription() {
        return dishDescription;
    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChefUID() {
        return chefUID;
    }

    public void setChefUID(String chefUID) {
        this.chefUID = chefUID;
    }

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
