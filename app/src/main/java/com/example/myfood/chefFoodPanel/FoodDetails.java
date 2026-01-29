package com.example.myfood.chefFoodPanel;

public class FoodDetails {

    public String Categorie, Dishes, Quantity, Price, Description, ImageURL, RandomUID, Chefid,time;

    public FoodDetails(String categorie, String dishName, String quantity, String price, String description, String imageURL, String randomUID, String chefid,String Time) {
        Categorie = categorie;
        Dishes = dishName;
        Quantity = quantity;
        Price = price;
        Description = description;
        ImageURL = imageURL;
        RandomUID = randomUID;
        Chefid = chefid;
        time=Time;
    }
}