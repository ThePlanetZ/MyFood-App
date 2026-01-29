package com.example.myfood.deliveryFoodPanel;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrdersAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> acceptedOrderList;

    public AcceptedOrdersAdapter(Context context, List<Order> acceptedOrderList) {
        mContext = context;
        this.acceptedOrderList = acceptedOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.delivery_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = acceptedOrderList.get(position);

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

        DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference("Chef").child(order.getChefUID());
        chefRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chefName = dataSnapshot.child("Last Name").getValue(String.class);
                    String cheflName = dataSnapshot.child("First Name").getValue(String.class);
                    String chefPhone = dataSnapshot.child("Mobile No").getValue(String.class);
                    String chefAddress = dataSnapshot.child("City").getValue(String.class);
                    String chefAddressa = dataSnapshot.child("Area").getValue(String.class);
                    String chefAddressh = dataSnapshot.child("House").getValue(String.class);
                    String chefCity = dataSnapshot.child("City").getValue(String.class);

                    holder.textViewChefName.setText("Chef Name: " + chefName+" "+cheflName);
                    holder.textViewChefPhone.setText("Chef Phone: " + chefPhone);
                    holder.textViewChefAddress.setText("Chef Address: " + chefAddress+", "+chefAddressa+", "+chefAddressh);
                    holder.textViewChefCity.setText("Chef City: " + chefCity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        holder.textViewDishName.setText("Dish Name: " + order.getDishName());
        holder.textViewDishPrice.setText("Price: " + order.getDishPrice());
        // Handle "Order delivered!" button click
        holder.btnConfirmdelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a dialog to confirm delivery
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm Order Delivery");
                builder.setMessage("Are you sure the order has been delivered?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If confirmed, send notifications
                        sendDeliveryNotification(order.getChefUID(), order.getCustomerUID());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    Order order;

    private void sendDeliveryNotification(String chefUID, String customerUID) {
        // Send notification to chef
        sendNotificationToChef(chefUID, "Your order has been delivered.");

        // Send notification to customer
        sendNotificationToCustomer(customerUID, "Your order has been delivered. Enjoy!");

        Toast.makeText(mContext, "Notifications sent successfully", Toast.LENGTH_SHORT).show();
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

    private void sendNotificationToCustomer(String customerUID, String message) {
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
                            notificationObj.put("title", "Delivery Status");
                            notificationObj.put("body", message);
                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("to", customerFCMToken);
                            sendNotification(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PendingAdapter", "Customer FCM token is null");
                    }
                } else {
                    Log.e("PendingAdapter", "Customer FCM token does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PendingAdapter", "Error retrieving customer FCM token: " + databaseError.getMessage());
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
        return acceptedOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCustomerName;
        TextView textViewCustomerPhone;
        TextView textViewCustomerAddress;
        TextView textViewCustomerCity;
        TextView textViewDishName;
        TextView textViewDishPrice;
        TextView textViewQuantity;
        TextView textViewChefName;
        TextView textViewChefAddress;
        TextView textViewChefPhone;
        TextView textViewChefCity;
        Button btnConfirmdelivery;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewCustomerPhone = itemView.findViewById(R.id.textViewCustomerPhone);
            textViewCustomerAddress = itemView.findViewById(R.id.textViewCustomerAddress);
            textViewCustomerCity = itemView.findViewById(R.id.textViewCustomerCity);
            textViewDishName = itemView.findViewById(R.id.textViewDishName);
            textViewDishPrice = itemView.findViewById(R.id.textViewDishPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewChefName = itemView.findViewById(R.id.textViewChefName);
            textViewChefAddress = itemView.findViewById(R.id.textViewChefAddress);
            textViewChefPhone = itemView.findViewById(R.id.textViewChefPhone);
            textViewChefCity = itemView.findViewById(R.id.textViewChefCity);
            btnConfirmdelivery = itemView.findViewById(R.id.btnConfirmdelivery);
        }
    }
}
