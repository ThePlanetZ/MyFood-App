package com.example.myfood.deliveryFoodPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.Order;
import com.example.myfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryShipOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private AcceptedOrdersAdapter adapter;
    private List<Order> acceptedOrdersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deliveryshiporder, container, false);
        getActivity().setTitle("Ship Orders");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        recyclerView = v.findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        acceptedOrdersList = new ArrayList<>();
        adapter = new AcceptedOrdersAdapter(getContext(), acceptedOrdersList);
        recyclerView.setAdapter(adapter);

        // Fetch accepted orders with deliveryStatus "OnTheWay" from Firebase
        fetchAcceptedOrders();

        return v;
    }

    private void fetchAcceptedOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        Query query = ordersRef.orderByChild("deliveryStatus").equalTo("OnTheWay");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptedOrdersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        acceptedOrdersList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}