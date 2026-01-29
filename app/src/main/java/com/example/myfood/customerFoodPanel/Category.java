package com.example.myfood.customerFoodPanel;

public class Category {
    private String name;
    private String imageURL;

    public Category() {
        // Default constructor required for Firebase
    }

    public Category(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
