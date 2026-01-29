package com.example.myfood.chefFoodPanel;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.DeliveryPerson;
import com.example.myfood.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder> {

    private List<DeliveryPerson> deliveryList;
    private Context context;

    public DeliveryAdapter(List<DeliveryPerson> deliveryList, Context context) {
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_card, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryPerson delivery = deliveryList.get(position);
        holder.bind(delivery);

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderIDDialog(delivery.getUid());
            }
        });
    }

    private void showOrderIDDialog(String deliveryUid) {
        // Inflate custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send, null);

        // Find views in the custom layout
        EditText orderIdInput = dialogView.findViewById(R.id.emailBox);
        Button sendButton = dialogView.findViewById(R.id.btnsend);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        // Build custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = orderIdInput.getText().toString().trim();
                if (!orderId.isEmpty()) {
                    // Dismiss the dialog
                    dialog.dismiss();
                    // Send notification
                    sendNotificationToDelivery(deliveryUid, orderId);
                } else {
                    Toast.makeText(context, "Please enter Order ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
    }


    private void sendNotificationToDelivery(String deliveryUid, String orderId) {
        try {
            DatabaseReference deliveryRef = FirebaseDatabase.getInstance().getReference("User").child(deliveryUid).child("token");
            deliveryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String deliveryFCMToken = dataSnapshot.getValue(String.class);
                        if (deliveryFCMToken != null) {
                            // Once the token is retrieved, send the notification
                            sendNotificationToDeliveryPerson(deliveryFCMToken);
                            updateDeliveryStatus(orderId,deliveryUid); // Update delivery status
                        } else {
                            Log.e("DeliveryAdapter", "Delivery person FCM token is null");
                        }
                    } else {
                        Log.e("DeliveryAdapter", "Delivery person FCM token does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DeliveryAdapter", "Error retrieving delivery person FCM token: " + databaseError.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("DeliveryAdapter", "Error sending notification to delivery person: " + e.getMessage());
        }
    }

    private void sendNotificationToDeliveryPerson(String deliveryFCMToken) {
        try {
            // Prepare the JSON payload for the notification
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "New Delivery Order ");
            notificationObj.put("body", "You have a new order!");
            // Add any additional data you want to send with the notification
            jsonObject.put("notification", notificationObj);
            jsonObject.put("to", deliveryFCMToken);

            // Call the method to send the notification using FCM
            callApi(jsonObject);

        } catch (Exception e) {
            Log.e("sendNotificationToDeliveryPerson", "Error sending notification to delivery person: " + e.getMessage());
        }
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAAKxowAgw:APA91bEKtlct0Hu3Nl4hBg9Ww2sQ-4TU8xc6j4bpceGEFtC8Yv5I-uxze-Vvh7tdlrBPuWTyHeu9ehE1gMEwqnXVGJX-_fyYNz0UvdQgQxy1RUnle3hpOnkfo4ymHtpHz4T9NTGIkNGF")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    private void updateDeliveryStatus(String orderId, String deliveryUid) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        ordersRef.child("deliveryStatus").setValue("pending");
        ordersRef.child("deliveryUID").setValue(deliveryUid)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DeliveryAdapter", "Delivery status updated successfully");
                    } else {
                        Log.e("DeliveryAdapter", "Error updating delivery status: " + task.getException().getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView deliveryNameTextView;
        private TextView deliveryPhoneTextView;
        private TextView deliveryAddressTextView;
        private TextView deliveryCityTextView;
        private Button sendButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryNameTextView = itemView.findViewById(R.id.delivery_name);
            deliveryPhoneTextView = itemView.findViewById(R.id.delivery_phone);
            deliveryAddressTextView = itemView.findViewById(R.id.delivery_adress);
            deliveryCityTextView = itemView.findViewById(R.id.delivery_city);
            sendButton= itemView.findViewById(R.id.send);
        }

        public void bind(DeliveryPerson delivery) {
            deliveryNameTextView.setText(delivery.getFirstName() + " " + delivery.getLastName());
            deliveryPhoneTextView.setText("Phone number : "+delivery.getMobileNo());
            deliveryAddressTextView.setText("Address: " + delivery.getAdress());
            deliveryCityTextView.setText("City: " + delivery.getCity());
        }
    }
}