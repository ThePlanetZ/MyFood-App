package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener{

    private RecyclerView recyclerView;
    private RecyclerView categorieRecyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerAdapter adapter;
    private TextView textViewCustomerName;
    private ImageView imageView;
    private EditText editText;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_home, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        textViewCustomerName = v.findViewById(R.id.textView);
        imageView = v.findViewById(R.id.imageView);
        editText = v.findViewById(R.id.editText);
        recyclerView = v.findViewById(R.id.recycle_customer);
        categorieRecyclerView = v.findViewById(R.id.categorie_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        categorieRecyclerView.setHasFixedSize(true);
        categorieRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        updateDishModelList = new ArrayList<>();
        retrieveCustomerName();
        retrieveCustomerImage();
        customerDishes();
        retrieveCategories();
        initializeAdapter();
        setupCategoryRecyclerView();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    // Refresh the page by reloading the original data
                    customerDishes();
                }
            }
        });

        return v;
    }
    private void setupCategoryRecyclerView() {
        // Initialize CategoryAdapter with all required arguments
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList, this); // Pass the listener

        // Set adapter to RecyclerView
        categorieRecyclerView.setAdapter(categoryAdapter);
    }

    private void displayDishesByCategory(Category category) {
        Log.d("CategoryAdapter", "displayDishesByCategory called for category: " + category.getName());

        DatabaseReference foodDetailsRef = FirebaseDatabase.getInstance().getReference("FoodDetails");

        foodDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    updateDishModelList.clear();
                    for (DataSnapshot citySnapshot : snapshot.getChildren()) {
                        for (DataSnapshot areaSnapshot : citySnapshot.getChildren()) {
                            for (DataSnapshot chefSnapshot : areaSnapshot.getChildren()) {
                                String chefId = chefSnapshot.getKey();
                                for (DataSnapshot dishSnapshot : chefSnapshot.getChildren()) {
                                    UpdateDishModel updateDishModel = dishSnapshot.getValue(UpdateDishModel.class);
                                    if (updateDishModel != null) {
                                        updateDishModel.setChefId(chefId);
                                        if (updateDishModel.getCategorie().equals(category.getName())) {
                                            updateDishModelList.add(updateDishModel);
                                        }
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
            }
        });
    }

    private void initializeAdapter() {
        adapter = new CustomerAdapter(requireContext(), updateDishModelList);
        recyclerView.setAdapter(adapter);
    }

    private void retrieveCustomerName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String customerUID = currentUser.getUid();
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerUID);
            customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("First Name").getValue(String.class);
                        if (firstName != null) {
                            textViewCustomerName.setText("Hello " + firstName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DatabaseError", "Failed to retrieve customer name: " + error.getMessage());
                }
            });
        }
    }

    private void retrieveCustomerImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String customerUID = currentUser.getUid();
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerUID);
            customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profileImageUrl = snapshot.child("Profile image").getValue(String.class);
                        if (profileImageUrl != null) {
                            Glide.with(getContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.prfl1)
                                    .into(imageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DatabaseError", "Failed to retrieve customer image: " + error.getMessage());
                }
            });
        }
    }

    private void retrieveCategories() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }
                categoryAdapter = new CategoryAdapter(requireContext(), categories, CustomerHomeFragment.this);
                categorieRecyclerView.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Failed to retrieve categories: " + error.getMessage());
            }
        });
    }

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
                                String chefId = chefSnapshot.getKey();
                                for (DataSnapshot dishSnapshot : chefSnapshot.getChildren()) {
                                    UpdateDishModel updateDishModel = dishSnapshot.getValue(UpdateDishModel.class);
                                    if (updateDishModel != null) {
                                        updateDishModel.setChefId(chefId);
                                        updateDishModelList.add(updateDishModel);
                                    } else {
                                        Log.e("DataSnapshotError", "Failed to parse UpdateDishModel: " + dishSnapshot.getKey());
                                    }
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    initializeAdapter();
                } catch (Exception e) {
                    Log.e("DataSnapshotError", "Error processing DataSnapshot", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        Log.d("CategoryAdapter", "set click listener hmmmmm");

        displayDishesByCategory(category);
    }
}
