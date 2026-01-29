package com.example.myfood.chefFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.Order;
import com.example.myfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChefPendingOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChefPendingAdapter adapter;
    private List<Order> pendingOrdersList;

    private DatabaseReference ordersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chef_pendingorders, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pending_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pendingOrdersList = new ArrayList<>();
        adapter = new ChefPendingAdapter(getContext(), pendingOrdersList);
        recyclerView.setAdapter(adapter);

        // Retrieve pending orders from Firebase
        retrievePendingOrders();

        return view;
    }

    private void retrievePendingOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String chefUid = currentUser.getUid();
            ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

            // Listen for changes in the database
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pendingOrdersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null && order.getChefUID().equals(chefUid) && order.getStatus().equals("Pending")) {
                            pendingOrdersList.add(order);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.e("ChefPendingOrderFragment", "Database error: " + databaseError.getMessage());
                    // You can display a toast message or show a dialog to inform the user about the error
                    Toast.makeText(getContext(), "Failed to retrieve data. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

