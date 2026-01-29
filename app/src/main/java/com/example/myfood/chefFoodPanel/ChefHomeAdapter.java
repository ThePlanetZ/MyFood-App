package com.example.myfood.chefFoodPanel;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class ChefHomeAdapter extends RecyclerView.Adapter<ChefHomeAdapter.ViewHolder> {

    private Context mcont;
    private List<UpdateDishModel> updateDishModelList;

    public ChefHomeAdapter(Context context, List<UpdateDishModel> updateDishModelList) {
        this.updateDishModelList = updateDishModelList;
        this.mcont = context;
    }

    @NonNull
    @Override
    public ChefHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcont).inflate(R.layout.chefmenu_update_delete, parent, false);
        return new ChefHomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChefHomeAdapter.ViewHolder holder, int position) {

        final UpdateDishModel updateDishModel = updateDishModelList.get(position);

        // Load dish image using Glide
        Glide.with(mcont)
                .load(updateDishModel.getImageURL()) // Assuming getImageURL() returns the URL of the dish image
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.error1) // Image to display in case of an error
                .into(holder.dishImageView);

        holder.dishName.setText(updateDishModel.getDishes());
        holder.price.setText("Price :"+updateDishModel.getPrice());
        holder.quantity.setText("Quantity :"+updateDishModel.getQuantity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcont, UpdateDelete_Dish.class);
                intent.putExtra("updatedeletedish", updateDishModel.getRandomUID());
                mcont.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return updateDishModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImageView;
        TextView dishName,price,quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dishImageView = itemView.findViewById(R.id.dish_image);
            dishName = itemView.findViewById(R.id.dish_name);
            price=itemView.findViewById(R.id.dish_price);
            quantity=itemView.findViewById(R.id.dish_quantity);
        }
    }
}
