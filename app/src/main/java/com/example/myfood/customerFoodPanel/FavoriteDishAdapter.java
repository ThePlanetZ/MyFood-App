package com.example.myfood.customerFoodPanel;



// FavoriteDishAdapter.java


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfood.UpdateDishModel;

import com.bumptech.glide.Glide;
import com.example.myfood.R;

import java.util.List;

public class FavoriteDishAdapter extends RecyclerView.Adapter<FavoriteDishAdapter.ViewHolder> {

    private Context mContext;
    private List<UpdateDishModel> mDishList;

    public FavoriteDishAdapter(Context context, List<UpdateDishModel> dishList) {
        mContext = context;
        mDishList = dishList;
    }

    // Interface to handle click events
    public interface OnLikeClickListener {
        void onLikeClick(int position);
    }

    private OnLikeClickListener mListener;

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UpdateDishModel dish = mDishList.get(position);

        holder.dishNameTextView.setText(dish.getDishes());
        holder.dishDescriptionTextView.setText(dish.getDescription());
        holder.dishPriceTextView.setText(dish.getPrice());

        // Load dish image using Glide library
        Glide.with(mContext)
                .load(dish.getImageURL())
                .into(holder.dishImageView);

        // Set OnClickListener for the like button
        holder.likeImageView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onLikeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDishList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImageView;
        TextView dishNameTextView;
        TextView dishDescriptionTextView;
        TextView dishPriceTextView;
        ImageView likeImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dishImageView = itemView.findViewById(R.id.imageViewDish);
            dishNameTextView = itemView.findViewById(R.id.textViewDishName);
            dishDescriptionTextView = itemView.findViewById(R.id.textViewDishDescription);
            dishPriceTextView = itemView.findViewById(R.id.textViewDishPrice);
            likeImageView = itemView.findViewById(R.id.imageViewLike);
           }
   }
}