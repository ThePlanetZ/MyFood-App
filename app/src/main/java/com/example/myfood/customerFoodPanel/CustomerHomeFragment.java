package com.example.myfood.customerFoodPanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {

    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerAdapter adapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView = v.findViewById(R.id.recycle_customer); // Replace with your actual RecyclerView ID
        searchView = v.findViewById(R.id.searchView); // Assuming you have a SearchView in your layout
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateDishModelList = new ArrayList<>(); // You can initialize it with your data
        // Call a method to populate the updateDishModelList with customer dishes
        customerDishes();

        adapter = new CustomerAdapter(getContext(), updateDishModelList);
        recyclerView.setAdapter(adapter);

        setupSearchView();

        return v;
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDishes(newText);
                return true;
            }
        });
    }

    private void filterDishes(String query) {
        List<UpdateDishModel> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(updateDishModelList);
        } else {
            for (UpdateDishModel dish : updateDishModelList) {
                if (dish.getDishes().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(dish);
                }
            }
        }

        adapter.filterList(filteredList);
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
