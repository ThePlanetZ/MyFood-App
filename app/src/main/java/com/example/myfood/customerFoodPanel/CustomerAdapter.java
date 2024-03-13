// CustomerAdapter.java
package com.example.myfood.customerFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.DishViewHolder> {

    private final Context context;
    private List<UpdateDishModel> dishList; // Replace DishModel with the actual model class for dishes

    // Constructor
    public CustomerAdapter(Context context, List<UpdateDishModel> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_menudish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        UpdateDishModel dish = dishList.get(position);

        holder.dishName.setText(dish.getDishes());
        holder.dishDescription.setText(dish.getDescription());
        holder.dishPrice.setText(dish.getPrice());

        // Load dish image using Glide or any other image loading library
        Glide.with(context)
                .load(dish.getImageURL())
                .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                .into(holder.dishImage);

        // Inside the onBindViewHolder method
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
                    context.startActivity(intent);
                } else {
                    Log.e("CustomerAdapter", "Clicked dish is null");
                }
            }
        });

    }
        @Override
    public int getItemCount() {
        return dishList.size();
    }

    // Add a filterList method to update the adapter data
    public void filterList(List<UpdateDishModel> filteredList) {
        dishList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName, dishDescription, dishPrice;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage = itemView.findViewById(R.id.imageViewDish); // Replace with your actual ImageView ID
            dishName = itemView.findViewById(R.id.Dishname); // Replace with your actual TextView ID
            dishDescription = itemView.findViewById(R.id.textViewDishDescription); // Replace with your actual TextView ID
            dishPrice = itemView.findViewById(R.id.DishPrice); // Replace with your actual TextView ID
        }
    }
}
