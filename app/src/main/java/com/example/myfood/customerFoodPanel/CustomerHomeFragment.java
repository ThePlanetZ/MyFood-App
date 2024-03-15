package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment implements OnAddToCartClickListener{

    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_home, null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView = v.findViewById(R.id.recycle_customer); // Replace with your actual RecyclerView ID
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateDishModelList = new ArrayList<>(); // You can initialize it with your data
        // Call a method to populate the updateDishModelList with customer dishes
         customerDishes();

        adapter = new CustomerAdapter(getContext(), updateDishModelList);
        recyclerView.setAdapter(adapter);
        adapter.setOnAddToCartClickListener(this);


        return v;
    }
    @Override
    public void onAddToCartClick(int position, String dishName, String imageUrl, String price) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String customerUID = currentUser.getUid();
            Log.d("Customer UID", customerUID);
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(customerUID);
            String cartItemId = cartRef.push().getKey();

            CartItem cartItem = new CartItem(dishName, imageUrl, price, 1);
            cartRef.child(cartItemId).setValue(cartItem);
            Log.e("Error33","onAddMethod called");
        } else {
            Log.e("Error", "No user is currently signed in.");
        }
    }

    // Add a method to fetch customer dishes from the database and update the updateDishModelList
    private void customerDishes() {
        DatabaseReference foodDetailsRef = FirebaseDatabase.getInstance().getReference("FoodDetails");

        foodDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    updateDishModelList.clear();

                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        for (DataSnapshot areaSnapshot : citySnapshot.getChildren()) {
                            for (DataSnapshot chefSnapshot : areaSnapshot.getChildren()) {
                                for (DataSnapshot dishSnapshot : chefSnapshot.getChildren()) {
                                    UpdateDishModel updateDishModel = dishSnapshot.getValue(UpdateDishModel.class);
                                    if (updateDishModel != null) {
                                        updateDishModelList.add(updateDishModel);
                                        Log.d("DataSize", "Number of dishes: " + updateDishModelList.size());

                                    } else {
                                        Log.e("DataSnapshotError", "Failed to parse UpdateDishModel: " + dishSnapshot.getKey());
                                    }
                                }
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("DataSnapshotError", "Error processing DataSnapshot", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Database error: " + error.getMessage());
                // Handle database error if needed
            }
        });
    }


}
