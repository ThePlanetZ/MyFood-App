package com.example.myfood.chefFoodPanel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.Order;
import com.example.myfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChefOrderAdapter extends RecyclerView.Adapter<ChefOrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;

    public ChefOrderAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chef_orders, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrderList.get(position);

        // Set dish details
        holder.textViewOrderId.setText(order.getOrderId());
        holder.textViewDishName.setText(order.getDishName());
        holder.textViewDishPrice.setText( order.getDishPrice());

        holder.copyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = order.getOrderId();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Order ID", orderId);
                clipboard.setPrimaryClip(clip);

                // Optionally, you can show a toast message to indicate that the order ID has been copied
                Toast.makeText(mContext, "Order ID copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        // Fetch customer details based on customer UID
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(order.getCustomerUID());
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerName = dataSnapshot.child("First Name").getValue(String.class);
                    String customerPhone = dataSnapshot.child("Mobile No").getValue(String.class);
                    String customerAddress = dataSnapshot.child("Local Address").getValue(String.class);
                    String customerCity = dataSnapshot.child("City").getValue(String.class);

                    // Set customer details
                    holder.textViewCustomerName.setText(customerName);
                    holder.textViewCustomerPhone.setText(customerPhone);
                    holder.textViewCustomerAddress.setText( customerAddress);
                    holder.textViewCustomerCity.setText(customerCity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDishName,textViewOrderId;
        TextView textViewDishPrice;
        TextView textViewCustomerName;
        TextView textViewCustomerPhone;
        TextView textViewCustomerAddress;
        TextView textViewCustomerCity;
        ImageView copyIcon;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDishName = itemView.findViewById(R.id.textViewDishName);
            textViewDishPrice = itemView.findViewById(R.id.textViewDishPrice);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewCustomerPhone = itemView.findViewById(R.id.textViewCustomerPhone);
            textViewCustomerAddress = itemView.findViewById(R.id.textViewCustomerAddress);
            textViewCustomerCity = itemView.findViewById(R.id.textViewCustomerCity);
            textViewOrderId = itemView.findViewById(R.id.orderId);
            copyIcon = itemView.findViewById(R.id.copyIcon);

        }
    }
}
