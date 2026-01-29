package com.example.myfood.deliveryFoodPanel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.LocationData;
import com.example.myfood.Order;
import com.example.myfood.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPendingOrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private List<Order> pendingOrdersList;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public DeliveryPendingOrderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deliverypendingorder, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Request location permissions
        requestLocationPermissions();

        // Initialize RecyclerView and adapter
        recyclerView = v.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pendingOrdersList = new ArrayList<>();
        pendingAdapter = new PendingAdapter(requireContext(), pendingOrdersList);
        recyclerView.setAdapter(pendingAdapter);

        loadPendingOrders();
        return v;
    }
    private void loadPendingOrders() {
        // Get current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle this case
            return;
        }
        String userId = currentUser.getUid();

        // Create a Firebase database reference to orders
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Query to fetch orders with delivery status pending and delivery UID matching current user's UID
        ordersRef.orderByChild("deliveryStatus").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pendingOrdersList.clear();
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null && order.getDeliveryUID() != null && order.getDeliveryUID().equals(userId)) {
                                String deliveryUid = order.getDeliveryUID();
                                if (deliveryUid != null) {
                                    Log.d("OrderDeliveryUID", "Delivery UID: " + deliveryUid);
                                    if (deliveryUid.equals(userId)) {
                                        pendingOrdersList.add(order);
                                    }
                                } else {
                                    Log.d("OrderDeliveryUID", "Delivery UID is null for order: " + order.getOrderId());
                                }
                            } else {
                                Log.d("OrderParsing", "Failed to parse order from snapshot: " + orderSnapshot.getKey());
                            }
                        }
                        // Notify the adapter about the data changes
                        pendingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Toast.makeText(requireContext(), "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, proceed to check and request location updates
            checkAndRequestLocationUpdates();
        }
    }

    private void checkAndRequestLocationUpdates(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(requireActivity());

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(requireActivity(), locationSettingsResponse -> {
            // All location settings are satisfied. Proceed to get location
            getLocation();


        });

        task.addOnFailureListener(requireActivity(), e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            requestLocationPermissions();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Location retrieved successfully
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Store location in Firebase
                        storeLocationInFirebase(latitude, longitude);
                        refreshFragment();

                    } else {
                        // Handle case where location is null
                        Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    // Handle failure to retrieve location
                    Toast.makeText(requireContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void storeLocationInFirebase(double latitude, double longitude) {
        // Get current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a Firebase database reference
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("delivery_locations").child(userId);

        // Create a LocationData object with latitude, longitude, and timestamp
        LocationData locationData = new LocationData(latitude, longitude, System.currentTimeMillis(), userId);

        // Store location data in Firebase
        locationRef.setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Location stored successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to store location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // User enabled location settings. Proceed to get location.
                getLocation();
                

            } else {
                // User cancelled or declined the location settings change.
                Toast.makeText(requireContext(), "Location settings not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void refreshFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }
}
