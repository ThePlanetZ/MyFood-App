package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CustomerCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<CartItem> cartItemList;
    private CartAdapter adapter;

    private TextView txtTotalFee;
    private String customerUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customercart, container, false);

        recyclerView = view.findViewById(R.id.rvItemsInCart);
        txtTotalFee = view.findViewById(R.id.txtTotalFee);

        cartItemList = new ArrayList<>();

        // Initialize customerUID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            customerUID = currentUser.getUid();
        }

        adapter = new CartAdapter(getContext(), cartItemList, customerUID);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load cart items from Firebase
        loadCartItems();

        return view;
    }

    private void loadCartItems() {
        if (customerUID != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(customerUID);

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartItemList.clear();
                    int totalFee = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                        if (cartItem != null) {
                            cartItemList.add(cartItem);
                            totalFee += Integer.parseInt(cartItem.getPrice()) * cartItem.getQuantity();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    txtTotalFee.setText(String.valueOf(totalFee) + "DH");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }
}
