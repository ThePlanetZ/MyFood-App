package com.example.myfood.chefFoodPanel;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.myfood.DeliveryPerson;
import com.example.myfood.LocationData;
import com.example.myfood.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class map_chef extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;
    private static final double MAX_DISTANCE_THRESHOLD = 10000; // Maximum distance threshold in meters

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference deliveryLocationsRef;
    MyBottomSheetDialogFragment bottomSheetFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_map_chef, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Initialize the map view
        MapView mapView;
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        Button showBottomSheetButton = view.findViewById(R.id.showBottomSheetButton);
        showBottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationService();
            }
        });

    }

    private void checkLocationService() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPS is not enabled, prompt user to enable it
            enableLocationService();
        } else {
            // GPS is enabled, proceed to get location
            getLocation();
        }
    }

    private void enableLocationService() {
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
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Location retrieved successfully
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Store chef's location
                        storeChefLocation(latitude, longitude);

                        // Zoom to chef's location on the map
                        LatLng chefLatLng = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chefLatLng, 15));
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
    private void storeChefLocation(double latitude, double longitude) {
        // Get current chef's user ID
        String chefId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Create a new LocationData object with the chef's location
        LocationData locationData = new LocationData(latitude, longitude, System.currentTimeMillis(), chefId);

        // Get reference to the chef's location in the Firebase Realtime Database
        DatabaseReference chefLocationRef = FirebaseDatabase.getInstance().getReference("chefs_locations").child(chefId);

        // Store the chef's location in the database
        chefLocationRef.setValue(locationData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Chef location stored successfully
                        Toast.makeText(requireContext(), "Chef location stored successfully", Toast.LENGTH_SHORT).show();

                        // Fetch nearby delivery persons
                        fetchNearbyDeliveryPersons(latitude, longitude);
                    } else {
                        // Failed to store chef location
                        Toast.makeText(requireContext(), "Failed to store chef location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchNearbyDeliveryPersons(double chefLatitude, double chefLongitude) {
        DatabaseReference deliveryLocationsRef = FirebaseDatabase.getInstance().getReference("delivery_locations");

        deliveryLocationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DeliveryPerson> nearbyDeliveryPersons = new ArrayList<>();
                List<LocationData> nearbyDeliverylocal = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String deliveryPersonUid = snapshot.getKey();
                    LocationData deliveryPersonLocation = snapshot.getValue(LocationData.class);


                    if (deliveryPersonLocation != null) {
                        double distance = calculateDistance(new LatLng(chefLatitude, chefLongitude),
                                new LatLng(deliveryPersonLocation.getLatitude(), deliveryPersonLocation.getLongitude()));

                        if (distance <= MAX_DISTANCE_THRESHOLD) {
                            // Delivery person is within the maximum distance threshold
                            // Fetch delivery person's details from "DeliveryPerson" node
                            nearbyDeliverylocal.add(deliveryPersonLocation);
                            DatabaseReference deliveryPersonRef = FirebaseDatabase.getInstance().getReference("DeliveryPerson").child(deliveryPersonUid);
                            deliveryPersonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Delivery person's information found in the database
                                        String firstName = dataSnapshot.child("First Name").getValue(String.class);
                                        String lastName = dataSnapshot.child("Last Name").getValue(String.class);
                                        String phoneNumber = dataSnapshot.child("Mobile No").getValue(String.class);
                                        String adress = dataSnapshot.child("Adress").getValue(String.class);
                                        String city=dataSnapshot.child("City").getValue(String.class);

                                        // Create a DeliveryPerson object with fetched details
                                        DeliveryPerson deliveryPerson = new DeliveryPerson();
                                        deliveryPerson.setFirstName(firstName);
                                        deliveryPerson.setLastName(lastName);
                                        deliveryPerson.setMobileNo(phoneNumber);
                                        deliveryPerson.setAdress(adress);
                                        deliveryPerson.setCity(city);
                                        deliveryPerson.setUid(deliveryPersonUid);

                                        // Add delivery person to the list
                                        nearbyDeliveryPersons.add(deliveryPerson);
                                        // Check if there are nearby delivery persons

                                        // Show the bottom sheet fragment after fetching all nearby delivery persons
                                            // Check if bottomSheetFragment is null
                                            if (nearbyDeliveryPersons.size() == nearbyDeliverylocal.size()) {
                                                bottomSheetFragment = new MyBottomSheetDialogFragment(nearbyDeliveryPersons);
                                                bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
                                            }


                                        // Now you can display the delivery person's information
                                        // You can pass the list of nearbyDeliveryPersons to the BottomSheetDialogFragment
                                        // or handle it according to your requirement
                                        displayNearbyDeliveryPersons(nearbyDeliverylocal);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors
                                    Log.e("fetchNearbyDeliveryPersons", "Failed to retrieve delivery person's information: " + databaseError.getMessage());
                                }
                            });

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("fetchNearbyDeliveryPersons", "Failed to fetch delivery locations: " + databaseError.getMessage());
            }
        });
    }


    private void displayNearbyDeliveryPersons(List<LocationData> nearbyDeliveryPersons) {

        // Iterate through the list of nearby delivery persons and add markers to the map
        for (LocationData deliveryPerson : nearbyDeliveryPersons) {
            LatLng deliveryPersonLatLng = new LatLng(deliveryPerson.getLatitude(), deliveryPerson.getLongitude());
            // Retrieve the delivery person's UID from LocationData
            String deliveryPersonUid = deliveryPerson.getUserId();
            // Retrieve the delivery person's name from the DeliveryPersons node in Firebase
            DatabaseReference deliveryPersonRef = FirebaseDatabase.getInstance().getReference("DeliveryPerson").child(deliveryPersonUid);
            deliveryPersonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Delivery person's information found in the database
                        String firstName = dataSnapshot.child("First Name").getValue(String.class);
                        String lastName = dataSnapshot.child("Last Name").getValue(String.class);
                        String deliveryPersonName = firstName + " " + lastName;
                        // Add marker to the map with the delivery person's name as the title
                        mMap.addMarker(new MarkerOptions().position(deliveryPersonLatLng).title(deliveryPersonName));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                    Log.e("map_chef", "Failed to retrieve delivery person's information: " + databaseError.getMessage());
                }
            });
        }
    }


    private double calculateDistance(LatLng location1, LatLng location2) {
        // Method to calculate distance between two LatLng points using Haversine formula
        double lat1 = location1.latitude;
        double lon1 = location1.longitude;
        double lat2 = location2.latitude;
        double lon2 = location2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000; // Convert distance to meters
        return dist;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            // Permission granted, proceed to get location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            // Location retrieved successfully
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Store chef's location
                            storeChefLocation(latitude, longitude);

                            // Zoom to chef's location on the map
                            LatLng chefLatLng = new LatLng(latitude, longitude);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chefLatLng, 15));

                            // Fetch and display nearby delivery persons
//                            fetchNearbyDeliveryPersons(latitude, longitude);
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
    }
}
