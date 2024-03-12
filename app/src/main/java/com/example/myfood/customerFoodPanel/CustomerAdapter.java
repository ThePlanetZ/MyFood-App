package com.example.myfood.customerFoodPanel;

import android.content.Context;
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
        holder.dishQuantity.setText(dish.getQuantity());

        // Load dish image using Glide or any other image loading library
        Glide.with(context)
                .load(dish.getImageURL())
                .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                .into(holder.dishImage);
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
        TextView dishName, dishDescription, dishPrice, dishQuantity;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage = itemView.findViewById(R.id.imageViewDish); // Replace with your actual ImageView ID
            dishName = itemView.findViewById(R.id.Dishname); // Replace with your actual TextView ID
            dishDescription = itemView.findViewById(R.id.textViewDishDescription); // Replace with your actual TextView ID
            dishPrice = itemView.findViewById(R.id.DishPrice); // Replace with your actual TextView ID
            dishQuantity = itemView.findViewById(R.id.DishQuantity); // Replace with your actual TextView ID
        }
    }
}