package com.example.myfood.customerFoodPanel;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.UpdateDishModel;
import com.example.myfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteDishAdapter adapter;
    private List<UpdateDishModel> dishList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customercart, container, false);

        // Initialize RecyclerView and dishList
        recyclerView = view.findViewById(R.id.recyclerViewFavoriteDishes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dishList = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        // Retrieve dishes from Firebase
        retrieveDishesFromFirebase();

        return view;
    }

    private void retrieveDishesFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userId);

            favoritesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dishList.clear();
                    for (DataSnapshot favoriteSnapshot : dataSnapshot.getChildren()) {
                        Map<String, String> dishData = (Map<String, String>) favoriteSnapshot.getValue();
                        if (dishData != null) {
                            String dishName = dishData.get("dishName");
                            String dishDescription = dishData.get("dishDescription");
                            String dishImageURL = dishData.get("dishImageURL");
                            String dishPrice = dishData.get("dishPrice");

                            // Create UpdateDishModel object and add it to the list
                            UpdateDishModel dish = new UpdateDishModel();
                            dish.setDishes(dishName);
                            dish.setDescription(dishDescription);
                            dish.setImageURL(dishImageURL);
                            dish.setPrice(dishPrice);
                            dishList.add(dish);
                        }
                    }
                    // Initialize and set adapter to RecyclerView
                    adapter = new FavoriteDishAdapter(getActivity(), dishList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
}