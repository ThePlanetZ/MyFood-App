package com.example.myfood.customerFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.DishViewHolder> implements Filterable {

    private final Context context;
    private List<UpdateDishModel> dishList;
    private List<UpdateDishModel> filteredList;

    public CustomerAdapter(Context context, List<UpdateDishModel> dishList) {
        this.context = context;
        this.dishList = dishList;
        this.filteredList = new ArrayList<>(dishList);
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_menudish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        UpdateDishModel dish = filteredList.get(position);

        // Set dish information
        holder.dishName.setText(dish.getDishes());
        holder.dishDescription.setText("Description: " + dish.getDescription());
        holder.dishPrice.setText(dish.getPrice() + "DH");
        holder.dishQuantity.setText("Quantity: " + dish.getQuantity());
        holder.dishCategory.setText( dish.getCategorie());

        // Call retrieveChefInfo() method to fetch and display chef information
        retrieveChefInfo(dish.getChefId(), holder);

        // Load dish image using Glide or any other image loading library
        Glide.with(context)
                .load(dish.getImageURL())
                .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                .into(holder.dishImage);

        // Set onClickListener for item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dish != null) {
                    // Pass data to DishDetailsActivity
                    Intent intent = new Intent(context, DishDetailsActivity.class);
                    intent.putExtra("dishName", dish.getDishes());
                    intent.putExtra("dishDescription", dish.getDescription());
                    intent.putExtra("dishPrice", dish.getPrice());
                    intent.putExtra("dishImageURL", dish.getImageURL()); // Use getStringExtra for the image URL
                    intent.putExtra("chefId", dish.getChefId());
                    intent.putExtra("Categorie", dish.getCategorie());
                    context.startActivity(intent);
                } else {
                    Log.e("CustomerAdapter", "Clicked dish is null");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Implement getFilter method for search functionality

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase().trim();
                List<UpdateDishModel> tempList = new ArrayList<>();

                if (searchText.isEmpty()) {
                    tempList.addAll(dishList);
                } else {
                    Map<String, String> chefInfoMap = new HashMap<>(); // Map to store chef info

                    // Fetch all chef information
                    DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference("Chef");
                    chefRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot chefSnapshot : snapshot.getChildren()) {
                                String chefId = chefSnapshot.getKey();
                                String chefFirstName = chefSnapshot.child("First Name").getValue(String.class);
                                String chefCity = chefSnapshot.child("City").getValue(String.class);
                                chefInfoMap.put(chefId, chefFirstName.toLowerCase() + "|" + chefCity.toLowerCase());
                            }

                            // Perform search based on dish and chef information
                            for (UpdateDishModel dish : dishList) {
                                if (dish.getDishes().toLowerCase().contains(searchText) ||
                                        dish.getDescription().toLowerCase().contains(searchText)) {
                                    tempList.add(dish);
                                } else {
                                    String chefInfo = chefInfoMap.get(dish.getChefId());
                                    if (chefInfo != null && chefInfo.contains(searchText)) {
                                        tempList.add(dish);
                                    }
                                }
                            }

                            FilterResults filterResults = new FilterResults();
                            filterResults.values = tempList;
                            publishResults(charSequence, filterResults);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseError", "Failed to retrieve chef information: " + error.getMessage());
                        }
                    });
                }

                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.values != null) {
                    filteredList.clear();
                    filteredList.addAll((List<UpdateDishModel>) filterResults.values);
                    notifyDataSetChanged();
                }
            }

        };
    }




    // ViewHolder class
    public static class DishViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewChef;
        ImageView dishImage;
        TextView dishName, dishDescription, dishPrice, dishQuantity,dishCategory;
        TextView chefNameTextView, chefCityTextView;
        ImageView chefImageView;        TextView textViewAverageRating;


        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage = itemView.findViewById(R.id.imageViewDish);
            dishName = itemView.findViewById(R.id.Dishname);
            dishDescription = itemView.findViewById(R.id.DishDescription);
            dishPrice = itemView.findViewById(R.id.DishPrice);
            dishQuantity = itemView.findViewById(R.id.DishQuantity);
            dishCategory=itemView.findViewById(R.id.DishCategory);
            // Initialize views for chef information
            chefNameTextView = itemView.findViewById(R.id.ChefName);
            chefCityTextView = itemView.findViewById(R.id.ChefCity);
            imageViewChef = itemView.findViewById(R.id.imageViewChef);
            textViewAverageRating = itemView.findViewById(R.id.textViewRight);

        }
    }

    private void retrieveChefInfo(String chefId, DishViewHolder holder) {
        DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference("Chef").child(chefId);
        chefRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve chef's information
                    String firstName = snapshot.child("First Name").getValue(String.class);
                    String city = snapshot.child("City").getValue(String.class);
                    String profileImageUrl = snapshot.child("Profile image").getValue(String.class);

                    // Update the views in ViewHolder
                    holder.chefNameTextView.setText(firstName);
                    holder.chefCityTextView.setText(city);

                    // Load the image into the circular ImageView using Glide
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(context)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                                .error(R.drawable.profile224) // Error image if loading fails
                                .circleCrop() // Crop the image into a circle
                                .into(holder.imageViewChef); // Set the loaded image into the ImageView
                    } else {
                        // Handle null or empty profile image URL
                        holder.imageViewChef.setImageResource(R.drawable.profile224);
                    }

                    // Calculate and display the average rating
                    DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(chefId);
                    ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                double totalRating = 0;
                                long numRatings = 0;
                                for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                                    double rating = customerSnapshot.child("rating").getValue(Double.class);
                                    totalRating += rating;
                                    numRatings++;
                                }
                                if (numRatings > 0) {
                                    double averageRating = totalRating / numRatings;
                                    holder.textViewAverageRating.setText(String.format("%.1f", averageRating));
                                } else {
                                    holder.textViewAverageRating.setText("No ratings yet");
                                }
                            } else {
                                holder.textViewAverageRating.setText("No ratings yet");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to retrieve chef information: " + error.getMessage());
            }
        });
    }
}