package com.example.myfood.customerFoodPanel;

// CartItem.java
public class CartItem {
    private String itemName,ImageURL;
    private String price;
    private int quantity;
    private String ItemId;
    // Default constructor
    public CartItem() {
    }


    public CartItem(String itemName,String ImageURL, String price, int quantity,String ItemId) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.ImageURL= ImageURL;
        this.ItemId=ItemId;
    }
    public CartItem(String itemName,String ImageURL, String price, int quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.ImageURL= ImageURL;

    }
    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String ItemId) {
        this.ItemId = ItemId;
    }
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getImageURL() {
        return ImageURL;}

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
