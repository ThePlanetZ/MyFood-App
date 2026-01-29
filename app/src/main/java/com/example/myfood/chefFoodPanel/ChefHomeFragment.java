package com.example.myfood.chefFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChefHomeFragment extends Fragment {

    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private ChefHomeAdapter adapter;
    DatabaseReference dataa;
    private String City,Area;
    TextView chefNameTextView; // TextView to display chef's name
    CircleImageView chefImageView; // CircleImageView to display chef's image

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chef_home, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        recyclerView = v.findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateDishModelList = new ArrayList<>();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dataa = FirebaseDatabase.getInstance().getReference("Chef").child(userid);
        dataa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Retrieve values from the database
                    String cityFromDB = dataSnapshot.child("City").getValue(String.class);
                    String areaFromDB = dataSnapshot.child("Area").getValue(String.class);

                    if (cityFromDB != null && areaFromDB != null) {
                        // Assign retrieved values to City and Area variables
                        City = cityFromDB;
                        Area = areaFromDB;
                        chefDishes(); // Call the method to fetch chef dishes
                    } else {
                        // Handle the case where City or Area is null
                        Log.e("ChefHomeFragment", "City or Area is null");
                    }

                    // Retrieve values from the database
                    String chefName = dataSnapshot.child("First Name").getValue(String.class);
                    String chefImageUrl = dataSnapshot.child("Profile image").getValue(String.class);

                    // Set chef's name to the TextView
                    chefNameTextView = v.findViewById(R.id.textView);
                    chefNameTextView.setText("Hello "+chefName);

                    // Load chef's image using Glide into the CircleImageView
                    chefImageView = v.findViewById(R.id.imageView);
                    Glide.with(requireContext())
                            .load(chefImageUrl)
                            .placeholder(R.drawable.placeholder) // Placeholder image while loading
                            .error(R.drawable.error1) // Image to display in case of an error
                            .transform(new CircleCrop()) // Apply circular transformation
                            .into(chefImageView);
                    // Fetch chef dishes after setting chef's data
                    chefDishes();
                } catch (Exception e) {
                    // Handle any potential exceptions
                    Log.e("ChefHomeFragment", "Error retrieving chef data: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        return v;
    }

    private void chefDishes() {

        String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails").child(City).child(Area).child(useridd);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateDishModelList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    UpdateDishModel updateDishModel = snapshot1.getValue(UpdateDishModel.class);
                    updateDishModelList.add(updateDishModel);
                }
                adapter = new ChefHomeAdapter(getContext(),updateDishModelList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
         });
    }
}