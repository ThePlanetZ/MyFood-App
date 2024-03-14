package com.example.myfood;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    private DatabaseReference cartRef;
    private static final String TAG = "FirebaseDatabaseHelper";

    public FirebaseDatabaseHelper() {
        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        cartRef = firebaseDatabase.getReference("Cart");
    }

    // Method to update the quantity of an item in the cart
    public void updateCartItemQuantity(String customerUID, String itemId, int quantity) {
        if (customerUID == null) {
            Log.e(TAG, "Customer UID is null");
            return;
        }
        if (itemId == null) {
            Log.e(TAG, "Item ID is null");
            return;
        }

        DatabaseReference cartItemsRef = cartRef.child(customerUID);

        // Retrieve the specific item from the cart
        cartItemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Update the quantity
                    dataSnapshot.getRef().child("quantity").setValue(quantity)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Quantity updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Failed to update quantity: " + e.getMessage());
                                }
                            });
                } else {
                    Log.e(TAG, "Item not found in the cart");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

}
