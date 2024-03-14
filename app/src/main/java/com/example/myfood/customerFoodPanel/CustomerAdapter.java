package com.example.myfood.customerFoodPanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfood.R;
import com.example.myfood.UpdateDishModel;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.DishViewHolder> {

    private final Context context;
    private final List<UpdateDishModel> dishList;
    private OnAddToCartClickListener mListener;



    public CustomerAdapter(Context context, List<UpdateDishModel> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    public void setOnAddToCartClickListener(OnAddToCartClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_menudish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UpdateDishModel dish = dishList.get(position);

        holder.dishName.setText(dish.getDishes());
        holder.dishDescription.setText("Description: " + dish.getDescription());
        holder.dishPrice.setText(dish.getPrice() + "DH");
        holder.dishQuantity.setText("Quantity: " + dish.getQuantity());

        // Load dish image using Glide or any other image loading library
        Glide.with(context)
                .load(dish.getImageURL())
                .placeholder(R.drawable.placeholder) // Replace with a placeholder image
                .into(holder.dishImage);

        // Set a click listener for the ADD button
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddToCartClick(position, dish.getDishes(), dish.getImageURL(), dish.getPrice());                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    // ViewHolder class
    public static class DishViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImage;
        TextView dishName, dishDescription, dishPrice, dishQuantity;
        ImageButton btnAddToCart;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);

            dishImage = itemView.findViewById(R.id.imageViewDish);
            dishName = itemView.findViewById(R.id.Dishname);
            dishDescription = itemView.findViewById(R.id.textViewDishDescription);
            dishPrice = itemView.findViewById(R.id.DishPrice);
            dishQuantity = itemView.findViewById(R.id.DishQuantity);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
