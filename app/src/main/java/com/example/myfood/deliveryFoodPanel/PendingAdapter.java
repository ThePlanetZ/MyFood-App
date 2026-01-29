package com.example.myfood.deliveryFoodPanel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> orderList;

    public PendingAdapter(Context context, List<Order> orderList) {
        mContext = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_delivery_pending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Bind dish information
        holder.textViewDishName.setText(" Dish name : "+order.getDishName());
        holder.textViewDishPrice.setText(" Price : "+order.getDishPrice());

        // Fetch customer information based on customer UID
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Chef").child(order.getChefUID());
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve customer details
                    String customerName = dataSnapshot.child("First Name").getValue(String.class);
                    String customerlName = dataSnapshot.child("Last Name").getValue(String.class);
                    String customerPhone = dataSnapshot.child("Mobile No").getValue(String.class);
                    String customerArea = dataSnapshot.child("Area").getValue(String.class);
                    String customerhouse = dataSnapshot.child("House").getValue(String.class);

                    String customerCity = dataSnapshot.child("City").getValue(String.class);

                    // Bind customer details
                    holder.textViewCustomerName.setText(" Chef Name : "+customerName +" "+customerlName);
                    holder.textViewCustomerPhone.setText(" Chef Phone : "+customerPhone);
                    holder.chefAdress.setText(" Chef Adresse : "+customerCity+","+customerArea+","+customerhouse);
                    holder.textViewCustomerCity.setText(" Chef City : "+customerCity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();            }
        });

        // Handle accept button click
        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDeliveryStatus(order.getOrderId());
                // Implement sending a notification to the chef here
                sendNotificationToChef(order.getChefUID());
            }
        });

        // Handle reject button click
        holder.buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle reject button click
                sendNotificationToChef(order.getChefUID(), "Your order has been rejected.");
                updateDeliveryStatusR(order.getOrderId());
            }
        });
    }
    private void updateDeliveryStatusR(String orderId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        ordersRef.child("deliveryStatus").setValue("Rejected")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PendingAdapter", "Delivery status updated successfully");
                    } else {
                        Log.e("PendingAdapter", "Error updating delivery status: " + task.getException().getMessage());
                    }
                });
    }
    private void sendNotificationToChef(String chefUID, String message) {
        DatabaseReference chefTokenRef = FirebaseDatabase.getInstance().getReference("User").child(chefUID).child("token");
        chefTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chefFCMToken = dataSnapshot.getValue(String.class);
                    if (chefFCMToken != null) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject notificationObj = new JSONObject();
                        try {
                            notificationObj.put("title", "Delivery Status");
                            notificationObj.put("body", message);
                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("to", chefFCMToken);
                            sendNotification(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PendingAdapter", "Chef FCM token is null");
                    }
                } else {
                    Log.e("PendingAdapter", "Chef FCM token does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PendingAdapter", "Error retrieving chef FCM token: " + databaseError.getMessage());
            }
        });
    }

    private void updateDeliveryStatus(String orderId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        ordersRef.child("deliveryStatus").setValue("OnTheWay")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PendingAdapter", "Delivery status updated successfully");
                    } else {
                        Log.e("PendingAdapter", "Error updating delivery status: " + task.getException().getMessage());
                    }
                });
    }

    private void sendNotificationToChef(String chefUID) {
        Log.d("PendingAdapter", chefUID);
        DatabaseReference chefTokenRef = FirebaseDatabase.getInstance().getReference("User").child(chefUID).child("token");
        chefTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chefFCMToken = dataSnapshot.getValue(String.class);
                    if (chefFCMToken != null) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject notificationObj = new JSONObject();
                        try {
                            notificationObj.put("title", "Delivery Status");
                            notificationObj.put("body", "Delivery is on his way.");
                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("to", chefFCMToken);
                            sendNotification(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PendingAdapter", "Chef FCM token is null");
                    }
                } else {
                    Log.e("PendingAdapter", "Chef FCM token does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PendingAdapter", "Error retrieving chef FCM token: " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(JSONObject jsonObject) {
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
                Log.e("PendingAdapter", "Failed to send notification: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("PendingAdapter", "Failed to send notification: " + response.body().string());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDishName, textViewDishPrice, textViewCustomerName, textViewCustomerPhone, textViewCustomerCity,chefAdress;
        Button buttonAccept, buttonReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDishName = itemView.findViewById(R.id.textViewDishName);
            textViewDishPrice = itemView.findViewById(R.id.textViewDishPrice);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewCustomerPhone = itemView.findViewById(R.id.textViewCustomerPhone);
            textViewCustomerCity = itemView.findViewById(R.id.chefCity);
            chefAdress = itemView.findViewById(R.id.chefAdress);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }
}