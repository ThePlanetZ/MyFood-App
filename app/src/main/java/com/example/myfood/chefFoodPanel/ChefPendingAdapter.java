package com.example.myfood.chefFoodPanel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myfood.Order;
import com.example.myfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChefPendingAdapter extends RecyclerView.Adapter<ChefPendingAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;

    public ChefPendingAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chef_pending, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mOrderList.get(position);

        // Set dish details
        holder.textViewDishName.setText("Dish Name : "+order.getDishName());
        holder.textViewDishPrice.setText("Price: " + order.getDishPrice()+"DH");


        // Load image using Glide
        Glide.with(mContext)
                .load(order.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error1)
                .into(holder.imageViewDish);

        // Retrieve customer information
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(order.getCustomerUID());
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerName = dataSnapshot.child("Last Name").getValue(String.class);
                    String customerfName = dataSnapshot.child("First Name").getValue(String.class);
                    String customerPhone = dataSnapshot.child("Mobile No").getValue(String.class);
                    String customerAddress = dataSnapshot.child("Local Address").getValue(String.class);
                    String customerCity = dataSnapshot.child("City").getValue(String.class);

                    // Set customer information to TextViews
                    holder.textViewCustomerName.setText("Customer Name: " + customerName+" "+customerfName);
                    holder.textViewCustomerPhone.setText("Customer Phone: " + customerPhone);
                    holder.textViewCustomerAddress.setText("Customer Address: " + customerAddress);
                    holder.textViewCustomerCity.setText("Customer City: " + customerCity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Set click listener for the "Accept" button
        holder.buttonAccept.setOnClickListener(view -> {
            // Update the status of the order to "OnPrepare"
            updateOrderStatus(order);
        });
        holder.buttonReject.setOnClickListener(view -> {
            deleteOrder(order);
        });

    }
    private void updateOrderStatus(Order order) {
        // Get the reference to the specific order in the Firebase Realtime Database
        String orderId = order.getOrderId();
        if (orderId != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
            // Update the status of the order to "OnPrepare"
            orderRef.child("status").setValue("InProgress")
                    .addOnSuccessListener(aVoid -> {
                        // Status updated successfully
                        Toast.makeText(mContext, "Order accepted successfully", Toast.LENGTH_SHORT).show();
                        sendNotificationToCustomer(order.getCustomerUID(), order.getDishName(),order.getImageUrl());
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update status
                        Log.e("ChefPendingAdapter", "Failed to update order status: " + e.getMessage());
                        Toast.makeText(mContext, "Failed to update order status. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("ChefPendingAdapter", "Order ID is null");
        }

    }
    private void deleteOrder(Order order) {
        // Send rejection notification to the customer
        sendRejectionNotificationToCustomer(order.getCustomerUID(), order.getDishName(), order.getImageUrl());

        // Get the reference to the specific order in the Firebase Realtime Database
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(order.getOrderId());

        // Remove the order from the database
        orderRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Order deleted successfully
                    Toast.makeText(mContext, "Order Rejected", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to delete order
                    Log.e("ChefPendingAdapter", "Failed to delete order: " + e.getMessage());
                    Toast.makeText(mContext, "Failed to delete order. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
    private void sendRejectionNotificationToCustomer(String customerUID, String dishName, String imageUrl) {
        DatabaseReference customerTokenRef = FirebaseDatabase.getInstance().getReference("User").child(customerUID).child("token");
        customerTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerFCMToken = dataSnapshot.getValue(String.class);
                    if (customerFCMToken != null) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject notificationObj = new JSONObject();
                        try {
                            notificationObj.put("title", "Order Status");
                            notificationObj.put("body", "Sorry, your order for " + dishName + " has been rejected.");
                            notificationObj.put("image", imageUrl);
                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("to", customerFCMToken);
                            callApi(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ChefPendingAdapter", "Customer FCM token is null");
                    }
                } else {
                    Log.e("ChefPendingAdapter", "Customer FCM token does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChefPendingAdapter", "Error retrieving customer FCM token: " + databaseError.getMessage());
            }
        });
    }
    private void sendNotificationToCustomer(String customerUID, String dishName, String imageUrl) {
        DatabaseReference customerTokenRef = FirebaseDatabase.getInstance().getReference("User").child(customerUID).child("token");
        customerTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerFCMToken = dataSnapshot.getValue(String.class);
                    if (customerFCMToken != null) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject notificationObj = new JSONObject();
                        try {
                            notificationObj.put("title", "Order Status");
                            notificationObj.put("body", "Your order for " + dishName + " has been accepted.");
                            notificationObj.put("image", imageUrl);
                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("to", customerFCMToken);
                            callApi(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ChefPendingAdapter", "Customer FCM token is null");
                    }
                } else {
                    Log.e("ChefPendingAdapter", "Customer FCM token does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChefPendingAdapter", "Error retrieving customer FCM token: " + databaseError.getMessage());
            }
        });
    }


    private void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAKxowAgw:APA91bEKtlct0Hu3Nl4hBg9Ww2sQ-4TU8xc6j4bpceGEFtC8Yv5I-uxze-Vvh7tdlrBPuWTyHeu9ehE1gMEwqnXVGJX-_fyYNz0UvdQgQxy1RUnle3hpOnkfo4ymHtpHz4T9NTGIkNGF")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ChefPendingAdapter", "Failed to send notification: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("ChefPendingAdapter", "Failed to send notification: " + response.body().string());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDishName;
        TextView textViewDishDescription;
        TextView textViewDishPrice;
        ImageView imageViewDish;
        Button buttonAccept;
        Button buttonReject;
        TextView textViewCustomerName; // Declare TextView for Customer Name
        TextView textViewCustomerPhone; // Declare TextView for Customer Phone
        TextView textViewCustomerAddress; // Declare TextView for Customer Address
        TextView textViewCustomerCity; // Declare TextView for Customer City

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDishName = itemView.findViewById(R.id.textViewDishName);

            textViewDishPrice = itemView.findViewById(R.id.textViewDishPrice);
            imageViewDish = itemView.findViewById(R.id.imageViewDish);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonReject= itemView.findViewById(R.id.buttonReject);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName); // Initialize TextView for Customer Name
            textViewCustomerPhone = itemView.findViewById(R.id.textViewCustomerPhone); // Initialize TextView for Customer Phone
            textViewCustomerAddress = itemView.findViewById(R.id.textViewCustomerAddress); // Initialize TextView for Customer Address
            textViewCustomerCity = itemView.findViewById(R.id.textViewCustomerCity);
        }
    }
}
