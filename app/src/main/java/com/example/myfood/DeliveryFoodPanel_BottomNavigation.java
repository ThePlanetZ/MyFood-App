package com.example.myfood;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myfood.deliveryFoodPanel.DeliveryPendingOrderFragment;
import com.example.myfood.deliveryFoodPanel.DeliveryShipOrderFragment;
import com.example.myfood.deliveryFoodPanel.Delivery_profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeliveryFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_food_panel_bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.deliveryBottom);
        navigationView.setOnNavigationItemSelectedListener(this);
      // Set the default fragment (ChefHomeFragment) when the activity starts
        loaddeliveryfragment(new DeliveryPendingOrderFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.shiporders) {
            fragment = new DeliveryShipOrderFragment();
        } else if (item.getItemId() == R.id.pendingorders) {
            fragment = new DeliveryPendingOrderFragment();
        } else if (item.getItemId() == R.id.delivery_prfl) {
            fragment = new Delivery_profile();
        }

        return loaddeliveryfragment(fragment);
    }


    private boolean loaddeliveryfragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,fragment).commit();
            return true;
        }
        return false;
    }
}