package com.example.myfood.customerFoodPanel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DishDetailsActivity extends AppCompatActivity {
    String chefId;
    String dishName;String dishDescription;String dishPrice;String dishImageURL,Categorie; String chefFCMToken;
    private List<UpdateDishModel> favoriteDishesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        UpdateDishModel dish = getIntent().getParcelableExtra("dish");


        // Retrieve dish details from Intent
         dishName = getIntent().getStringExtra("dishName");
         dishDescription = getIntent().getStringExtra("dishDescription");
         dishPrice = getIntent().getStringExtra("dishPrice");
         dishImageURL = getIntent().getStringExtra("dishImageURL");
         chefId = getIntent().getStringExtra("chefId");
         Categorie= getIntent().getStringExtra("Categorie");

        // Perform null checks before using the data
        if (dishName != null && dishDescription != null && dishPrice != null && dishImageURL != null) {
            // Display details in the activity
            ImageView dishImageView = findViewById(R.id.imageView5);
            TextView dishNameTextView = findViewById(R.id.name);
            TextView dishDescriptionTextView = findViewById(R.id.textView8);
            TextView dishPriceTextView = findViewById(R.id.price);
            Button buttonOrderNow = findViewById(R.id.button);
            ImageView heartImageView = findViewById(R.id.imageView7);

            // Load dish image using Glide or any other image loading library
            Glide.with(this)
                    .load(dishImageURL)
                    .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                    .into(dishImageView);

            dishNameTextView.setText(dishName);
            dishDescriptionTextView.setText(dishDescription);
            dishPriceTextView.setText("Price: " + dishPrice + "DH");

            heartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToFavorites(dish);
                }
            });


            ///////////////
            // Order now :
            buttonOrderNow.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DishDetailsActivity.this);
                        builder.setTitle("Confirm Order");
                        builder.setMessage("Are you sure you want to place this order?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendNotificationToChef();
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User cancelled the order, do nothing or provide feedback
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();


            });
        } else {
            Log.e("DishDetailsActivity", "One or more dish details are null");
            // You might want to handle this situation, for example, by finishing the activity
            finish();
        }

    }


    private void addToFavorites(UpdateDishModel dish) {
        favoriteDishesList.add(dish);
         saveFavoriteDishesToPrefs();

            // Get the current user's ID

                // Show a Toast message to indicate that the dish has been added to favorites
                Toast.makeText(getApplicationContext(), "Dish added to favorites", Toast.LENGTH_SHORT).show();


            }

//        favoriteDishesList.add(dish);
//        saveFavoriteDishesToPrefs(); // Save the updated list to SharedPreferences
        // Show a Toast message to indicate that the dish has been added to favorites


    // Method to save the favorite dishes list to SharedPreferences
    private void saveFavoriteDishesToPrefs() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyFoodPrefs", Context.MODE_PRIVATE);
        // Get SharedPreferences editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert the favoriteDishesList to a JSON string
        String favoriteDishesJson = new Gson().toJson(favoriteDishesList);
        // Save the JSON string to SharedPreferences
        editor.putString("favorite_dishes", favoriteDishesJson);
        // Apply changes
        editor.apply();
   }


    private String getCurrentUserUID() {
        // Get the current Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the current user exists
        if (currentUser != null) {
            // Return the UID of the current user
            return currentUser.getUid();
        } else {
            // If no user is signed in, return null or handle the situation accordingly
            return null;
        }
    }

    private void sendNotificationToChef() {
        try {
            Toast.makeText(DishDetailsActivity.this, "Order sent to the chef successfully, Please wait for chef response.", Toast.LENGTH_LONG).show();

            // Retrieve Chef id :
            DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference("User").child(chefId).child("token");
            chefRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String chefFCMToken = dataSnapshot.getValue(String.class);
                        if (chefFCMToken != null) {
                            Log.v("DishDetailsActivity", chefFCMToken);

                            // Once the token is retrieved, send the notification and store the order
                            sendNotificationAndStoreOrder(chefFCMToken);
                        } else {
                            Log.e("DishDetailsActivity", "Chef FCM token is null");
                        }
                    } else {
                        Log.e("DishDetailsActivity", "Chef FCM token does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DishDetailsActivity", "Error retrieving chef FCM token: " + databaseError.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("DishDetailsActivity", "Error sending notification to chef: " + e.getMessage());
        }
    }

    private void sendNotificationAndStoreOrder(String chefFCMToken) {
        try {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

            // Generate a unique order ID
            String orderId = ordersRef.push().getKey();

            // Create a HashMap to store order details
            HashMap<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("orderId",orderId);
            orderDetails.put("dishName", dishName);
            orderDetails.put("dishDescription", dishDescription);
            orderDetails.put("dishPrice", dishPrice);
            orderDetails.put("imageUrl", dishImageURL);
            orderDetails.put("chefUID", chefId); // Assuming you have chefId available
            orderDetails.put("customerUID", getCurrentUserUID()); // Assuming you have a method to get current user UID
            orderDetails.put("status", "Pending");
            orderDetails.put("Categorie", Categorie);



            // Set the order details under the unique order ID
            if (orderId != null) {
                ordersRef.child(orderId).setValue(orderDetails);
            }

            // Send the notification
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "New Order");
            notificationObj.put("body", "You have a new "+dishName+" order!");
            notificationObj.put("image",dishImageURL );
            jsonObject.put("notification", notificationObj);
            jsonObject.put("to", chefFCMToken);

            //
            // Add custom data to the notification payload for the action
            JSONObject dataObj = new JSONObject();
            dataObj.put("action", "view_pending");
            jsonObject.put("data", dataObj);

            callApi(jsonObject);

        } catch (Exception e) {
            Log.e("DishDetailsActivity", "Error sending notification and storing order: " + e.getMessage());
        }
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAKxowAgw:APA91bEKtlct0Hu3Nl4hBg9Ww2sQ-4TU8xc6j4bpceGEFtC8Yv5I-uxze-Vvh7tdlrBPuWTyHeu9ehE1gMEwqnXVGJX-_fyYNz0UvdQgQxy1RUnle3hpOnkfo4ymHtpHz4T9NTGIkNGF")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }


}
