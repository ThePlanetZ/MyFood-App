package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myfood.R;

public class DishDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details_customer);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Retrieve dish details from Intent
        String dishName = getIntent().getStringExtra("dishName");
        String dishDescription = getIntent().getStringExtra("dishDescription");
        String dishPrice = getIntent().getStringExtra("dishPrice");
        String dishImageURL = getIntent().getStringExtra("dishImageURL");

        // Perform null checks before using the data
        if (dishName != null && dishDescription != null && dishPrice != null && dishImageURL != null) {
            // Display details in the activity
            ImageView dishImageView = findViewById(R.id.imageView5);
            TextView dishNameTextView = findViewById(R.id.name);
            TextView dishDescriptionTextView = findViewById(R.id.textView8);
            TextView dishPriceTextView = findViewById(R.id.price);
            Button buttonAddToCart = findViewById(R.id.button);

            // Load dish image using Glide or any other image loading library
            Glide.with(this)
                    .load(dishImageURL)
                    .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                    .into(dishImageView);

            dishNameTextView.setText(dishName);
            dishDescriptionTextView.setText(dishDescription);
            dishPriceTextView.setText("Price: " + dishPrice + "DH");

            // Add your click listeners for checkout and add to cart buttons if needed
            // For now, they are just placeholders
            buttonAddToCart.setOnClickListener(view -> {
                // Handle add to cart button click
            });
        } else {
            Log.e("DishDetailsActivity", "One or more dish details are null");
            // You might want to handle this situation, for example, by finishing the activity
            finish();
        }
    }
}


