package com.example.myfood;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class activity_rating extends AppCompatActivity {
    private ImageView dishImageView;
    private TextView dishNameTextView;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        dishImageView = findViewById(R.id.dishImageView);
        dishNameTextView = findViewById(R.id.dishNameTextView);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);
        ratingTextView = findViewById(R.id.rating); // Adjusted: Initialize ratingTextView

        // Retrieve data from Intent extras
        String dishName = getIntent().getStringExtra("dishName");
        String dishImage = getIntent().getStringExtra("dishImage");

        // Set dish name
        dishNameTextView.setText(dishName);

        // Load dish image using Picasso or any other image loading library
        Picasso.get().load(dishImage).into(dishImageView);

        // Add an OnRatingBarChangeListener to update the TextView when the rating changes
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Update the TextView to display the selected rating
                ratingTextView.setText(String.valueOf(rating));
            }
        });

        // Implement your logic for rating and submitting feedback
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve rating and comment
                float rating = ratingBar.getRating();
                String comment = commentEditText.getText().toString();

                // You can handle the submission process here, such as sending data to Firebase or any other backend
                // For demonstration purposes, you can display a toast message
                submitRatingToFirebase(rating, comment);
            }
        });
    }

    private void submitRatingToFirebase(float rating, String comment) {
        // Retrieve chef UID and customer UID from Intent extras
        String chefUid = getIntent().getStringExtra("chefUid");
        String customerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ensure chefUid is not null
        if (chefUid == null) {
            Toast.makeText(this, "Error: chefUid is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Write the rating and comment to Firebase under the appropriate path
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings")
                .child(chefUid)
                .child(customerUid); // Store ratings under chefUid with customerUid as child node
        ratingsRef.child("rating").setValue(rating);
        ratingsRef.child("comment").setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity_rating.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                        // You can perform any additional actions here upon successful submission
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity_rating.this, "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}