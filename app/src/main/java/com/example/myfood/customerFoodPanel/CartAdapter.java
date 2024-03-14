package com.example.myfood.customerFoodPanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.FirebaseDatabaseHelper;
import com.example.myfood.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartItemList;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private final String customerUID;
    // Constructor
    public CartAdapter(Context context, List<CartItem> cartItemList,String customerUID) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        this.customerUID=customerUID;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mycart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.dishName.setText(cartItem.getItemName());
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.feeEachItem.setText(cartItem.getPrice());
        holder.totalEachItem.setText(String.valueOf(Integer.parseInt(cartItem.getPrice()) * cartItem.getQuantity()));

        Picasso.get().load(cartItem.getImageURL()).into(holder.dishImage);

        holder.minBtnCart.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            if (currentQuantity > 1) {
                currentQuantity--;
                cartItem.setQuantity(currentQuantity);
                if (customerUID != null && cartItem.getItemId() != null ){
                    // Update Firebase database
                    firebaseDatabaseHelper.updateCartItemQuantity(customerUID, cartItem.getItemId(), currentQuantity);
                }
                notifyDataSetChanged();
            }
        });

        holder.plusBtnCart.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            currentQuantity++;
            cartItem.setQuantity(currentQuantity);

                // Update Firebase database
                firebaseDatabaseHelper.updateCartItemQuantity(customerUID, cartItem.getItemId(), currentQuantity);

            notifyDataSetChanged();
        });
    }



    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // ViewHolder class
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView dishName, feeEachItem, totalEachItem, quantity;
        ImageView minBtnCart, plusBtnCart,dishImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.txtTitleCart);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            quantity = itemView.findViewById(R.id.numItems);
            dishImage = itemView.findViewById(R.id.picCart);
            minBtnCart = itemView.findViewById(R.id.minBtnCart);
            plusBtnCart = itemView.findViewById(R.id.plusBtnCart);
        }
    }
}
