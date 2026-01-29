package com.example.myfood.customerFoodPanel;

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
import com.example.myfood.Order;
import com.example.myfood.R;
import com.example.myfood.activity_rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;


    public OrderAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrderList.get(position);

        // Set dish details
        holder.textViewDishName.setText(order.getDishName());
        holder.textViewDishPrice.setText(order.getDishPrice());


        DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference("Chef").child(order.getChefUID());
        chefRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chefName = dataSnapshot.child("First Name").getValue(String.class);
                    holder.textViewChefName.setText("Chef: " + chefName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        ////////////////////////////

        // Set OnClickListener to handle item click
// Set OnClickListener to handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked order
                Order clickedOrder = mOrderList.get(position);

                // Pass the dish name, image URL, and chefUID to the RatingActivity
                Intent intent = new Intent(mContext, activity_rating.class);
                intent.putExtra("dishName", clickedOrder.getDishName());
                intent.putExtra("dishImage", clickedOrder.getImageUrl());
                intent.putExtra("chefUid", clickedOrder.getChefUID()); // Include chefUID
                mContext.startActivity(intent);
            }
        });


        // Load image using Glide
        Glide.with(mContext)
                .load(order.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageViewDish);

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDish;
        TextView textViewDishName;
        TextView textViewDishPrice;
        TextView textViewDeliveryPerson;
        TextView textViewChefName;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            textViewDishName = itemView.findViewById(R.id.textViewDishName);
            textViewDishPrice = itemView.findViewById(R.id.textViewDishPrice);
            textViewDeliveryPerson = itemView.findViewById(R.id.textViewDeliveryPerson);
            textViewChefName = itemView.findViewById(R.id.textViewChefName);
        }
    }
}